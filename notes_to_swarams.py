import pyaudio
import numpy as np
import os
import os.path
from note_utils import freqs, carnatic_keys, shifts
from scipy.io.wavfile import write

note_freqs = dict()

class Note:

	dur = 1 / 6

	def __init__(self, volume, frequency, duration=dur, prev=None):
		self.volume = volume
		self.duration = duration
		self.frequency = frequency
		self.prev = prev

	def is_first(self):
		return self.prev is None

	def is_repeat(self):
		return ((not self.is_first()) and self.prev.frequency == self.frequency)

	def __repr__(self):
		return 'Note({}, {}, {})'.format(self.volume, self.duration, self.frequency)

def init_note_freqs(pitch='C'):
	global note_freqs
	shift = shifts[pitch]
	new_freqs = np.roll(freqs, -shift)
	for i in range(1, shift + 1):
		new_freqs[-i] *= 2
	note_freqs = dict(zip(carnatic_keys, new_freqs))

def str_to_notes(song):
	last_note = None
	note_dur = Note.dur
	all_notes = []
	for c in song:
		if c == '(':
			note_dur /= 2
		elif c == ')':
			note_dur *= 2
		elif c == '*':
			last_note.frequency *= 2
		elif c == '/':
			last_note.frequency /= 2
		elif c == ',':
			last_note.duration += note_dur
		else:
			if last_note is not None:
				if (last_note.is_repeat()):
					all_notes.append(Note(0, 440, duration=Note.dur / 10))
					last_note.duration -= Note.dur / 10
				all_notes.append(last_note)
			last_note = Note(0.5, note_freqs[c], prev=last_note, duration=note_dur)
	all_notes.append(last_note)
	return all_notes

def output_notes(notes, outfile, fs=44100):
	samples = []
	for note in notes:
		v, d, f = note.volume, note.duration, note.frequency
		samples = np.append(samples, v*(np.sin(2*np.pi*np.arange(fs*d)*f/fs)).astype(np.float32))
		# for paFloat32 sample values must be in range [-1.0, 1.0]
	# play. May repeat with different volume values (if done interactively)
	np.save(outfile[:outfile.rfind('.')] + '.npy', samples)
	write(outfile, fs, samples)

def notation_to_audio(infile, pitch='C'):
	init_note_freqs(pitch)
	outfile = infile[:infile.rfind('.')] + '-' + pitch + '.wav'
	if os.path.isfile(outfile):
		os.remove(outfile)
	all_notes = []
	song = ''
	for line in open(infile):
		song += line.strip()
	all_notes = str_to_notes(song)
	try:
		output_notes(all_notes, outfile)
		print('Audio successfully outputted to', outfile)
	except:
		print('Audio output failed.')

notation = input('Input notation file name: ')
while True:
	pitch = input('Enter the pitch of the audio output: ')
	if pitch in shifts:
		break
	print('Invalid pitch')
notation_to_audio(notation, pitch)