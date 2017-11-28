import javax.sound.sampled.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;

public class MakeSound {

    private static double[] freqs = {261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392.00, 415.30, 440.00, 466.16, 493.88};

    public static void playSong(String path) {
        byte[] buf = new byte[1];
        Scanner scan = OpenFile.openToRead(path);
        SourceDataLine sdl = null;
        AudioFormat af = new AudioFormat((float)44100, 8, 1, true, false);
        try {
            sdl = AudioSystem.getSourceDataLine(af);
            sdl.open();
        } catch (LineUnavailableException e) {
            System.err.println("Error: Line not available");
            System.exit(1);
        }
        sdl.start();
        // int duration = Prompt.getInt("Enter a duration in milliseconds -> ", 0, Integer.MAX_VALUE);
        // double frequency = Prompt.getDouble("Enter a frequency of a note -> ");
        while (scan.hasNext()) {
            String song = scan.nextLine();
            ArrayList<Note> notes = readLine(song, 125.0);
            double minDuration = notes.get(0).getDuration();
            for (Note n : notes) {
                if (n.getDuration() < minDuration) {
                    minDuration = n.getDuration();
                }
            }
            for (int i = 0; i < notes.size(); i++) {
                Note n = notes.get(i);
                double d = n.getDuration(), f = n.getFreq(), v = n.getVolume();
                boolean isRepeat = (i > 0 && n.equals(notes.get(i - 1)) && notes.get(i - 1).getDuration() <= minDuration);
                if (isRepeat) {
                    d *= 0.95;
                    try {
                        Thread.sleep((int)(d / 0.95 * 0.05));
                    } catch (InterruptedException e) {
                        System.err.println("Error: Interrupted sleep");
                        System.exit(1);
                    }
                }
                for (int k = 0; k < d * (float)44100 / 1000; k++) {
                    double angle = k / ((float)44100 / f) * 2.0 * Math.PI;
                    buf[0] = (byte)(Math.sin(angle) * v);
                    sdl.write(buf, 0, 1);
                }
            }

        }
        scan.close();
        sdl.drain();
        sdl.stop();
    }

    public static ArrayList<Note> readLine(String song, double dur) {
        HashMap<Character, String> gamakams = new HashMap<Character, String>();
        gamakams.put('S', "S");
        gamakams.put('R', "GR");
        gamakams.put('G', "G");
        gamakams.put('m', "m");
        gamakams.put('P', "P");
        gamakams.put('D', "S*D");
        gamakams.put('N', "S*N");
        String lastNote = "";
        for (int i = 0; i < song.length(); i++) {
            char c = song.charAt(i);
            if (c == '*') {

            }
        }
        return getNotes(song, dur);
    }

    public static ArrayList<Note> getNotes(String song, double dur) {
        double frequency;
        double duration = dur;
        ArrayList<Note> notes = new ArrayList<Note>();
        for (int i = 0; i < song.length(); i++) {
            double s = duration;
            int j;
            int multFactor = 1;
            int divFactor = 1;
            int times = 0;
            for (int k = i + 1; k < song.length(); k++) {
                if (song.charAt(k) == '*') {
                    times++;
                    multFactor++;
                }
                else break;
            }
            int index = i;
            i += times;
            times = 0;
            for (int k = i + 1; k < song.length(); k++) {
                if (song.charAt(k) == '/') {
                    times++;
                    divFactor++;
                }
                else break;
            }
            i += times;
            times = 0;
            for (j = i + 1; j < song.length(); j++) {
                if (song.charAt(j) == ',') {
                    times++;
                    duration += s;
                }
                else break;
            }
            int ind = 0;
            switch (song.charAt(index)) {
                case 'S': ind = 0; break;
                case 'r': ind = 1; break;
                case 'R': ind = 2; break;
                case 'g': ind = 3; break;
                case 'G': ind = 4; break;
                case 'm': ind = 5; break;
                case 'M': ind = 6; break;
                case 'P': ind = 7; break;
                case 'd': ind = 8; break;
                case 'D': ind = 9; break;
                case 'n': ind = 10; break;
                case 'N': ind = 11; break;
            }
            frequency = freqs[ind];
            frequency *= multFactor;
            frequency /= divFactor;
            notes.add(new Note(frequency, duration, 100));
            duration = s;
            i += times;
        }
        return notes;    
    }

    public static void main(String[] args) throws Throwable {
        playSong(args[0]);
    }
}