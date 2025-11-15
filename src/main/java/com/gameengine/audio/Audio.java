package com.gameengine.audio;

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

public class Audio {
    private String filePath;
    private Thread playThread = null;
    private float volume = 0.2f;
    private boolean playing = false;

    public Audio(String filePath) throws Exception {
        this.filePath = filePath;
        File file = new File(filePath);
        if (file.exists()) {
            // Log.d("Audio(file)", "Loading audio from: " + filePath);
            try {
                FileInputStream fis = new FileInputStream(filePath);
                Bitstream bitstream = new Bitstream(fis);
                Header header = bitstream.readFrame();
                if (header != null) {
                    bitstream.close();
                    fis.close();
                } else {
                    // Log.e("Audio(file)", "Failed to read MP3 header from: " + filePath);
                }
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new java.io.FileNotFoundException("Audio file not found: " + filePath);
        }
    }

    public void play() throws Exception {
        if (this.playing) return;

        this.playing = true;
        this.playThread = new Thread(() -> {
            try {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
                Bitstream bitstream = new Bitstream(inputStream);

                Decoder decoder = new Decoder();
                AudioDevice device = FactoryRegistry.systemRegistry().createAudioDevice();
                device.open(decoder);

                while (this.playing) {
                    try {
                        Header header = bitstream.readFrame();
                        if (header == null) {
                            break;
                        }
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(header, bitstream);

                        // Apply volume control (0.0 to 1.0)
                        short[] buffer = output.getBuffer();
                        for (int i = 0; i < output.getBufferLength(); i++) {
                            buffer[i] = (short) (buffer[i] * this.volume);
                        }

                        device.write(buffer, 0, output.getBufferLength());
                    } catch (Exception e) {
                        // Log.e("Audio.play()", "Skipping. Error decoding audio frame: " + e.getMessage());
                    } finally {
                        bitstream.closeFrame();
                    }
                }
                device.flush();
                device.close();
            } catch (Exception e) {
            }
        }, "AudioPlayThread");
        this.playThread.setDaemon(true);
        this.playThread.start();
    }

    public void stop() {
        if (this.playThread != null) {
            this.playing = false;
            this.playThread.interrupt();
        }
    }
}
