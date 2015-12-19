import javax.sound.sampled.*;
import java.util.Scanner;

public class MakeSound {
    public static void main(String[] args) throws Throwable {
        byte[] buf = new byte[1];
        Scanner scan = OpenFile.openToRead("payphone.txt");
        AudioFormat af = new AudioFormat((float)44100, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open();
        sdl.start();
        double[] freqs = {261.63,293.66,329.63,349.23,392,440,493.88};
        // int duration = Prompt.getInt("Enter a duration in milliseconds -> ", 0, Integer.MAX_VALUE);
        // double frequency = Prompt.getDouble("Enter a frequency of a note -> ");
        while (scan.hasNext()) {
            String song = scan.nextLine();
            double frequency;
            double duration = 1000.0 / 8;
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
                int ind = song.charAt(index) - '1';
                frequency = freqs[ind];
                frequency *= multFactor;
                frequency /= divFactor;
                for(int k = 0; k < duration * (float)44100 / 1000; k++) {
                    double angle = k / ((float)44100 / frequency) * 2.0 * Math.PI;
                    buf[0] = (byte)(Math.sin(angle) * 100);
                    sdl.write(buf, 0, 1);
                }
                duration = s;
                i += times;
            }
        }
        scan.close();
        sdl.drain();
        sdl.stop();
    }
}