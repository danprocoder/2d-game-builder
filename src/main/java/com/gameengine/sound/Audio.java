package com.gameengine.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

import com.gamebuilder.util.Log;

public class Audio {
    private String filePath;
    private Thread playThread = null;

    public Audio(String filePath) throws Exception {
        this.filePath = filePath;
        File file = new File(filePath);
        if (file.exists()) {
            Log.d("Audio(file)", "Loading audio from: " + filePath);
            try {
                FileInputStream fis = new FileInputStream(filePath);
                Bitstream bitstream = new Bitstream(fis);
                Header header = bitstream.readFrame();
                if (header != null) {
                    Log.d(
                        "Audio(file)",
                        "MP3 Audio Info - Sample Rate: " + header.sample_frequency()
                        + ", Bitrate: " + header.bitrate() + " kbps"
                    );
                    bitstream.close();
                    fis.close();
                } else {
                    Log.e("Audio(file)", "Failed to read MP3 header from: " + filePath);
                }
            } catch (Exception e) {
                Log.e("Audio(file)", "Unsupported audio format: " + filePath);
                Log.e("Audio(file)", "Error: " + e.getMessage());
                Log.e("Audio(file)", "Try converting to PCM WAV format (16-bit, 44100Hz)");
                throw e;
            }
        } else {
            Log.e("Audio(file)", "Audio file not found: " + filePath);
            throw new java.io.FileNotFoundException("Audio file not found: " + filePath);
        }
    }

    public void play() throws Exception {
        this.playThread = new Thread(() -> {
            try {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
                Bitstream bitstream = new Bitstream(inputStream);

                Decoder decoder = new Decoder();
                AudioDevice device = FactoryRegistry.systemRegistry().createAudioDevice();
                device.open(decoder);

                while (true) {
                    try {
                        Header header = bitstream.readFrame();
                        if (header == null) {
                            break;
                        }
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(header, bitstream);
                        device.write(output.getBuffer(), 0, output.getBufferLength());
                    } catch (Exception e) {
                        Log.e("Audio.play()", "Skipping. Error decoding audio frame: " + e.getMessage());
                    } finally {
                        bitstream.closeFrame();
                    }
                }

                device.flush();
                device.close();
                Log.d("Audio.play()", "Playing audio: " + this.filePath);
            } catch (Exception e) {
                Log.e("Audio.play()", "Error playing audio: " + e.getMessage());
            }
        }, "AudioPlayThread");
        this.playThread.setDaemon(true);
        this.playThread.start();
    }

    public void stop() {
        if (this.playThread != null) {
            this.playThread.interrupt();
        }
    }
}
