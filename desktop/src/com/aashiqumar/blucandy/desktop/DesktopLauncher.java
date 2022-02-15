package com.aashiqumar.blucandy.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.aashiqumar.blucandy.BCapp;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.height = 640;
		config.width = 360;
		new LwjglApplication(new BCapp(), config);
	}
}
