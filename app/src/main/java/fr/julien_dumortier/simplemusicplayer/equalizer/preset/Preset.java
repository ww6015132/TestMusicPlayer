package fr.julien_dumortier.simplemusicplayer.equalizer.preset;

/**
 * 
 * @author jdumortier
 *
 *		
        0- Basses
        1- Aigus
        2- Basses et aigus
        3- Moyen
        4- Plat
        5- Rock
        6- Pop
        7- Dance
 *
 *
 *
 */
public class Preset {

	private static final int [][] mPreset = {
											/*BASSES*/{86, 78, 61, 54, 50},
											/*AIGUS*/{50, 50, 54, 64, 77},
											/*BASSES ET AIGUS*/{86, 77, 62, 77, 86},
											/*MOYEN*/{61, 50, 50, 50, 61},
											/*PLAT*/{50, 50, 50, 50, 50},
											/*DANCE*/{77, 50, 57, 61, 54},
											/*ROCK*/{84, 77, 57, 77, 82},
											/*POP*/{47, 61, 74, 61, 47}
											};
	
	private int [] mPercentPresets;
	
	public Preset(int posPreset) {
		if(mPreset.length>posPreset)
			mPercentPresets = mPreset[posPreset];
		else
			mPercentPresets = mPreset[0];
	}
	
	public int [] formatPreset(int nbBands, int minValue, int maxValue) {
		int [] formatedPercentPreset = formatPreset(nbBands);
		int [] formatedPreset = new int[formatedPercentPreset.length];
		int diff = maxValue + (minValue<0 ? -minValue : minValue);
		for(int i=0; i<formatedPercentPreset.length; i++) {
			formatedPreset[i] =  ((formatedPercentPreset[i]*diff)/100);
		}
		return formatedPreset;
	}
	
	private int [] formatPreset(int nbBands) {
		int []formatedPreset = new int[nbBands];
		
		if(nbBands>=(mPercentPresets.length*2)) {
			for(int i = 0; i<mPercentPresets.length; i++) {
				formatedPreset[i] = mPercentPresets[i];
				formatedPreset[i+1] = mPercentPresets[i];
			}
			if(nbBands>(mPercentPresets.length*2)) {
				for(int i = (mPercentPresets.length*2); i<nbBands; i++)
					formatedPreset[i] = mPercentPresets[mPercentPresets.length-1];
			}
		} else {
			for(int i = 0; i<mPercentPresets.length; i++) {
				formatedPreset[i] = mPercentPresets[i];
			}
			if(nbBands>mPercentPresets.length) {
				for(int i = mPercentPresets.length; i<nbBands; i++)
					formatedPreset[i] = mPercentPresets[mPercentPresets.length-1];
			}
		}
		return formatedPreset;
	}
}
