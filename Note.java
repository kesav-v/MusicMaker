public class Note {

	private double freq, duration, volume;

	public Note(double freq, double duration, double volume) {
		this.freq = freq;
		this.duration = duration;
		this.volume = volume;
	}

	public double getFreq() {return freq;}
	public double getDuration() {return duration;}
	public double getVolume() {return volume;}

	public boolean equals(Note n2) {
		return (this.freq == n2.freq && this.duration == n2.duration && this.volume == n2.volume);
	}
}