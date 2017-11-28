import pyaudio
import numpy as np
from note_utils import freqs, carnatic_keys, shifts
from scipy.io.wavfile import write

note_freqs = dict()

class Note:

	dur = 0.125

	def __init__(self, volume, duration=dur, frequency, prev=None):
		self.volume = volume
		self.duration = duration
		self.frequency = frequency
		self.prev = prev

	def is_first(self):
		return self.prev is None

	def is_repeat(self):
		return ((not is_first()) and self.prev.frequency == self.frequency and self.prev.duration = dur)

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
	all_notes = []
	for c in song:
		if c == '*':
			last_note.frequency *= 2
		elif c == '/':
			last_note.frequency /= 2
		elif c == ',':
			last_note.duration += Note.dur
		else:
			if last_note is not None:
				if (last_note.is_repeat()):
					all_notes.append(Note(0, Note.dur / 5, 440))
					last_note.duration -= Note.dur / 5
				all_notes.append(last_note)
			last_note = Note(0.5, Note.dur, note_freqs[c], last_note)
	all_notes.append(last_note)
	return all_notes

def output_notes(notes, outfile, fs=44100):
	samples = []
	for note in notes:
		v, d, f = note.volume, note.duration, note.frequency
		# All code beyond this point taken from https://stackoverflow.com/questions/8299303/generating-sine-wave-sound-in-python
		samples = np.append(samples, v*(np.sin(2*np.pi*np.arange(fs*d)*f/fs)).astype(np.float32))
		# for paFloat32 sample values must be in range [-1.0, 1.0]
	# play. May repeat with different volume values (if done interactively) 
	write(outfile, fs, samples)

def notation_to_audio(pitch, infile):
	init_note_freqs(pitch)
	outfile = infile[:infile.rfind('.')] + '.wav'
	all_notes = []
	length = 0
	for line in open(infile):
		length += len(line.strip())
		all_notes.extend(str_to_notes(line.strip()))
	try:
		output_notes(all_notes, outfile)
		print('Audio output succeeded!')
	except:
		print('Audio output failed.')

notation = input('Input notation file name: ')
output = input('Output audio file name (must be .wav): ')
pitch = input('Enter the pitch of the audio output: ')
notation_to_audio(pitch, notation)