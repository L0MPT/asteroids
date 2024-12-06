import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundPlayer {

	static ArrayList<Sound> sounds = new ArrayList<Sound>();
	static boolean mute = false;

	public static boolean searchSounds(String filePath) {
		if (sounds == null) {
			return false;
		}
		for (Sound sound : sounds) {
			if (sound.path.equals(filePath)) {
				return true;
			}
		}
		return false;
	}

	public static boolean searchSoundsStopped(String filePath) {
		if (sounds == null) {
			return false;
		}
		for (Sound sound : sounds) {
			if (sound.path.equals(filePath) && !sound.clip.isRunning()) {
				return true;
			}
		}
		return false;
	}

	public static int getSoundIndex(String filePath) {
		for (int i = 0; i < sounds.size(); i++) {
			if (sounds.get(i).path.equals(filePath)) {
				return i;
			}
		}
		System.err.println("Sound not found in getSoundIndex");
		return 0;
	}

	private static int getSoundIndexStopped(String filePath) {
		for (int i = 0; i < sounds.size(); i++) {
			if (sounds.get(i).path.equals(filePath) && !sounds.get(i).clip.isRunning()) {
				return i;
			}
		}
		System.err.println("Sound not found in getSoundIndexStopped");
		return 0;
	}

	public static void loadSound(String filePath) {
		if (!searchSoundsStopped(filePath)) {
			try {
				AudioInputStream audioInputStream = AudioSystem
						.getAudioInputStream(new File(filePath).getAbsoluteFile());

				Clip clip = AudioSystem.getClip();

				clip.open(audioInputStream);

				sounds.add(new Sound(clip, filePath));
			} catch (Exception ex) {
				System.err.println("Error loading sound");
				ex.printStackTrace();
			}
		}
	}

	public static void loadStoppedSound(String filePath) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());

			Clip clip = AudioSystem.getClip();

			clip.open(audioInputStream);

			sounds.add(new Sound(clip, filePath));
		} catch (Exception ex) {
			System.err.println("Error loading sound");
			ex.printStackTrace();
		}
	}

	public static void playSound(String path, Boolean decay) {
		// the next code is for playing sound files (wav format)
		// if (decay && Asteroids.getRumble() == 0)
		// return;
		if(mute) {
			return;
		}

		if (searchSounds(path)) {
			int soundIndex = getSoundIndexStopped(path);
			Sound sound = sounds.get(soundIndex);
			if (!sound.clip.isRunning()) {
				sound.play(decay);
			}
		} else {
			loadPlay(path, decay);
		}

	}

	private static void loadPlay(String path, Boolean decay) {
		loadSound(path);
		if (searchSounds(path)) {
			int soundIndex = getSoundIndexStopped(path);
			sounds.get(soundIndex).play(decay);
		} else {
			System.err.println("Error in playing sound");
		}
	}

	public static double getVolume(Clip clip) {
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		return (double) Math.pow(10.0, gainControl.getValue() / 20.0);
	}

	public static void setVolume(Clip clip, double volume) {
		if (volume < 0.0 || volume > 1.0)
			throw new IllegalArgumentException("Volume not valid: " + volume);
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(20f * (float) Math.log10(volume));
	}
	public static void muteToggle() {
		mute = !mute;
	}
}

class Sound {
	String path;
	Clip clip;

	Sound(Clip clip, String path) {
		this.clip = clip;
		this.path = path;

		clip.addLineListener(new LineListener() {
			public void update(LineEvent myLineEvent) {
				if (myLineEvent.getType() == LineEvent.Type.STOP)
					clip.setFramePosition(0);
			}
		});

	}

	void play(boolean decay) {
		if (decay) {
			SoundPlayer.setVolume(clip, (float) (Asteroids.getRumble() / Asteroids.getRumblemax()));
		}
		clip.start();
	}

	public String toString() {
		return path;
	}
}