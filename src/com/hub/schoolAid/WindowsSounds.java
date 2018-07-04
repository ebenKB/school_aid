package com.hub.schoolAid;

import java.awt.*;

public class WindowsSounds {
	public static void playWindowsSound() {
		Runnable sound1 = (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.default");
		if(sound1 != null) sound1.run();
	}
}
