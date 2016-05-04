package fr.julien_dumortier.simplemusicplayer.equalizer;

import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.equalizer.preset.Preset;
import fr.julien_dumortier.simplemusicplayer.service.BackgroundBinder;
import fr.julien_dumortier.simplemusicplayer.service.MusicPlayerService;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EqualizerActivity extends Activity {
	
	private static final int SESSION_ID_NOT_SETTED = 0;
	private ServiceConnection mConnection;		 
    private MusicPlayerService mService;
    private Equalizer mEqualizer;
    private int mAudioSessionId;
	private LinearLayout mLinearLayout;
	private VisualizerView mVisualizerView;
	private Visualizer mVisualizer;
    private int mPreset;
	private int[] mBandPreset;
	private Spinner mSpinner;
	private CompoundButton mToggleSwitch;	
	
	public void onCreate(Bundle bdl) {
		super.onCreate(bdl);
		setContentView(R.layout.equalizer_activity);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle(getResources().getString(R.string.equalizer));
	        mToggleSwitch = (CompoundButton) LayoutInflater.from(this).inflate(DisplayManager2.getSwitchStyle(this), null);
		 	mToggleSwitch.setChecked(EqualizerConfigurationDataBase.isActiveEqualizer(this));	
            mToggleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                	try {
                		if(mEqualizer!=null)
                			mEqualizer.setEnabled(isChecked);
                	} catch (Exception e) {}
                	EqualizerConfigurationDataBase.setActiveEqualizer(EqualizerActivity.this, isChecked);
                }
            });
            actionBar.setCustomView(mToggleSwitch, new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL | Gravity.RIGHT));
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		}
		
		findViewById(R.id.bg).setBackgroundColor(-13487566);
		mLinearLayout = (LinearLayout) findViewById(R.id.layout_seekbar);
		mAudioSessionId = SESSION_ID_NOT_SETTED;
		mConnection = new ServiceConnection() { 
			public void onServiceDisconnected(ComponentName name) {}
			public void onServiceConnected(ComponentName name,
					IBinder service) {	
					mService  = ((BackgroundBinder)service).getService();
					try {
						mAudioSessionId = mService.getMusicMediaPlayer().getMediaPlayer().getAudioSessionId();
					 	mEqualizer = mService.getEqualizer();
						setupEqualizerFxAndUI();
					 	mToggleSwitch.setChecked(mEqualizer.getEnabled());				        
				        mSpinner = (Spinner) findViewById(R.id.spinner1);
				        mSpinner.setBackgroundDrawable(DisplayManager2.getSpinnerStyleDrawable(EqualizerActivity.this));
				        if(mSpinner.getSelectedView()!=null && mSpinner.getSelectedView() instanceof TextView)
				        	((TextView)mSpinner.getSelectedView()).setTextColor(Color.WHITE);
				        if(mPreset!= EqualizerConfigurationDataBase.INACTIVE_PRESET)
				        	mSpinner.setSelection(mPreset);
				        else
				        	mSpinner.setSelection(mSpinner.getCount()-1);
				        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
								if(mSpinner.getSelectedView()!=null && mSpinner.getSelectedView() instanceof TextView)
						        	((TextView)mSpinner.getSelectedView()).setTextColor(Color.WHITE);
								if(pos != mSpinner.getCount()-1) {
									EqualizerConfigurationDataBase.setPreset(EqualizerActivity.this, pos);
									try {
										setupEqualizerFxAndUI();
									} catch (Exception e) {
										e.printStackTrace();
									}	
								} else {
									EqualizerConfigurationDataBase.setPreset(EqualizerActivity.this, EqualizerConfigurationDataBase.INACTIVE_PRESET);
								}
							}

							public void onNothingSelected(AdapterView<?> arg0) {
							}
						});
						((LinearLayout) findViewById(R.id.layout_vizualiseur)).addView(getVisualizerFxAndUI(mService, mAudioSessionId));
					} catch (Exception e) {
						Toast.makeText(EqualizerActivity.this, "Echec setup equalizer fx", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
			}
		};
		Intent intent = new Intent(this, MusicPlayerService.class);          
		bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    try {
		    ActionBar actionBar = getActionBar();
			if(actionBar!=null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setDisplayShowTitleEnabled(false);
	            actionBar.setTitle(getResources().getString(R.string.equalizer));
		        mToggleSwitch = (CompoundButton) LayoutInflater.from(this).inflate(DisplayManager2.getSwitchStyle(this), null);
			 	mToggleSwitch.setChecked(EqualizerConfigurationDataBase.isActiveEqualizer(this));	
	            mToggleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
	                	if(mEqualizer!=null)
	                		mEqualizer.setEnabled(isChecked);
	                	EqualizerConfigurationDataBase.setActiveEqualizer(EqualizerActivity.this, isChecked);
	                }
	            });
	            actionBar.setCustomView(mToggleSwitch, new ActionBar.LayoutParams(
	                    ActionBar.LayoutParams.WRAP_CONTENT,
	                    ActionBar.LayoutParams.WRAP_CONTENT,
	                    Gravity.CENTER_VERTICAL | Gravity.RIGHT));
	            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
			}
			((LinearLayout) findViewById(R.id.layout_vizualiseur)).removeAllViewsInLayout();
			setContentView(R.layout.equalizer_activity);
			findViewById(R.id.bg).setBackgroundColor(-13487566);
			mLinearLayout = (LinearLayout) findViewById(R.id.layout_seekbar);
	
			try {
				setupEqualizerFxAndUI();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				mToggleSwitch.setChecked(mEqualizer.getEnabled());	
			} catch (Exception e1) {
				e1.printStackTrace();
			}			        
	        mSpinner = (Spinner) findViewById(R.id.spinner1);
	        mSpinner.setBackgroundDrawable(DisplayManager2.getSpinnerStyleDrawable(EqualizerActivity.this));
	        if(mSpinner.getSelectedView()!=null && mSpinner.getSelectedView() instanceof TextView)
	        	((TextView)mSpinner.getSelectedView()).setTextColor(Color.WHITE);
	        if(mPreset!= EqualizerConfigurationDataBase.INACTIVE_PRESET)
	        	mSpinner.setSelection(mPreset);
	        else
	        	mSpinner.setSelection(mSpinner.getCount()-1);
	        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					if(mSpinner.getSelectedView()!=null && mSpinner.getSelectedView() instanceof TextView)
			        	((TextView)mSpinner.getSelectedView()).setTextColor(Color.WHITE);
					if(pos != mSpinner.getCount()-1) {
						EqualizerConfigurationDataBase.setPreset(EqualizerActivity.this, pos);
						try {
							setupEqualizerFxAndUI();
						} catch (Exception e) {
							e.printStackTrace();
						}	
					} else {
						EqualizerConfigurationDataBase.setPreset(EqualizerActivity.this, EqualizerConfigurationDataBase.INACTIVE_PRESET);
					}
				}
	
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
	        if(mVisualizerView!=null)
	        	((LinearLayout) findViewById(R.id.layout_vizualiseur)).addView(mVisualizerView);

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
	
	protected void onPause() {
        super.onPause();
        try {
    	saveConfiguration();
        mVisualizer.release();
        } catch(Exception e){}
	}
	
	 private void setupEqualizerFxAndUI() throws Exception {
			 try {
			 	mLinearLayout.removeAllViewsInLayout();
			 	int screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
			 	int screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		        final short bands = mEqualizer.getNumberOfBands();
	
		        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
		        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];
		       // final short maxEQLevel = (short) ( minEQLevel + 	        		(( maxLevel +  ( minEQLevel < 0 ? -minEQLevel:minEQLevel ))/2));
		        int defaultValue = ((maxEQLevel-( minEQLevel > 0 ? minEQLevel:-minEQLevel )/2));
		        boolean active = EqualizerConfigurationDataBase.isActiveEqualizer(this);
		        
	
			 	mPreset = EqualizerConfigurationDataBase.getPreset(this);
			 	if( mPreset!=EqualizerConfigurationDataBase.INACTIVE_PRESET)
			 		mBandPreset = new Preset(mPreset).formatPreset(bands, minEQLevel, maxEQLevel);
		        
		        for (short i = 0; i < bands; i++) {
		            final short band = i;
	
		            TextView freqTextView = new TextView(this);
		            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
		            		screenWidth/(bands+1),(int)(screenHeight*0.5*0.2)));
		            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		            freqTextView.setText((mEqualizer.getCenterFreq(band) / 1000) + " Hz");
		            freqTextView.setTextColor(Color.WHITE);
	
		            LinearLayout row = new LinearLayout(this);
		            row.setGravity(Gravity.CENTER_HORIZONTAL);
		            row.setOrientation(LinearLayout.VERTICAL); 
	
		            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
		            		LinearLayout.LayoutParams.WRAP_CONTENT,(int)(screenHeight*0.5*0.8));
		            VerticalSeekBar bar = new VerticalSeekBar(this);
		            bar.setProgressDrawable(DisplayManager2.getProgressStyleDrawable(this));
		            bar.setLayoutParams(layoutParams);
		            bar.setMax(maxEQLevel+(minEQLevel < 0 ? -minEQLevel:minEQLevel));
		            int level = (defaultValue < 0 ? -defaultValue:defaultValue);
		           // if(active) 
		            {
		            	if(mPreset==EqualizerConfigurationDataBase.INACTIVE_PRESET)
		            		level = EqualizerConfigurationDataBase.getBandLevel(this, i, defaultValue)+(minEQLevel < 0 ? -minEQLevel:minEQLevel);
		            	else
		            		level = /*(minEQLevel < 0 ? -minEQLevel:minEQLevel)-*/(mBandPreset[i] < 0 ? -mBandPreset[i]:mBandPreset[i]);
		            }
		            bar.setProgress(level);
		            try {
		            	mEqualizer.setBandLevel(band, (short) (level + minEQLevel));
		            } catch (Exception e) {}
		            bar.setThumb(DisplayManager2.getThumbStyleDrawable(this));
		            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		                public void onProgressChanged(SeekBar seekBar, int progress,
		                        boolean fromUser) {
		                	try {
			                	if(mPreset != EqualizerConfigurationDataBase.INACTIVE_PRESET)
			                		mSpinner.setSelection(mSpinner.getCount()-1, true);
			                	mPreset = EqualizerConfigurationDataBase.INACTIVE_PRESET;
			                    mEqualizer.setBandLevel(band, (short) (progress + minEQLevel));
		                	} catch (Exception e) {}
		                }
		                public void onStartTrackingTouch(SeekBar seekBar) {}
		                public void onStopTrackingTouch(SeekBar seekBar) {}
		            });
		            row.addView(bar);
		            row.addView(freqTextView);
		            mLinearLayout.addView(row);
		        }
	            mEqualizer.setEnabled(active);
			 }catch(Exception e){}
	    }
	 
	 	private void saveConfiguration() {
	 		int nbBands = mEqualizer.getNumberOfBands();
	 		for(short i=0;i<nbBands;i++) {
	 			EqualizerConfigurationDataBase.setBandLevel(this,i, mEqualizer.getBandLevel(i));
	 		}
	 		EqualizerConfigurationDataBase.setPreset(this, mPreset);
	 		EqualizerConfigurationDataBase.setActiveEqualizer(this, mEqualizer.getEnabled());
	 	}
	 
		public void onStop() {
			if(mConnection!=null) {
				try {
					unbindService(mConnection);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			super.onStop();
		}

		public boolean onCreateOptionsMenu(Menu menu) {
			ImageView v = (ImageView) findViewById(android.R.id.home);
			v.setImageResource(R.drawable.ic_casque);
			return true;
		}

		public boolean onOptionsItemSelected(MenuItem item) {
		        switch (item.getItemId()) {
		            case android.R.id.home:
		            	finish();
		                return true;
			        default:
			                return super.onOptionsItemSelected(item);
		        }
		}				
		
	    private static final float VISUALIZER_HEIGHT_DIP = 50f;
		private VisualizerView getVisualizerFxAndUI(Context context, int audioSessionId) {
	        // Create a VisualizerView (defined below), which will render the simplified audio
	        // wave form to a Canvas.
	        mVisualizerView = new VisualizerView(context);
	        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
	                ViewGroup.LayoutParams.FILL_PARENT,
	                (int)(VISUALIZER_HEIGHT_DIP * context.getResources().getDisplayMetrics().density)));

	        // Create the Visualizer object and attach it to our media player.
	        // you need write "<uses-permission android:name="android.permission.RECORD_AUDIO" />" in manifest.xml
	        mVisualizer = new Visualizer(audioSessionId);
	        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
	        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
	            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
	                    int samplingRate) {
	                mVisualizerView.updateVisualizer(bytes);
	            }

	            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}
	        }, Visualizer.getMaxCaptureRate() / 2, true, false);
	        mVisualizer.setEnabled(true);
	        return mVisualizerView;
	    }
		
		class VisualizerView extends View {
		    private byte[] mBytes;
		    private float[] mPoints;
		    private Rect mRect = new Rect();

		    private Paint mForePaint = new Paint();

		    public VisualizerView(Context context) {
		        super(context);
		        init();
		    }

		    private void init() {
		        mBytes = null;
		        mForePaint.setStrokeWidth(1f);
		        mForePaint.setAntiAlias(true);
		        mForePaint.setColor(DisplayManager2.getVisualizerColor(EqualizerActivity.this));
		    }

		    public void updateVisualizer(byte[] bytes) {
		        mBytes = bytes;
		        invalidate();
		    }

		    protected void onDraw(Canvas canvas) {
		        super.onDraw(canvas);

		        if (mBytes == null) {
		            return;
		        }

		        if (mPoints == null || mPoints.length < mBytes.length * 4) {
		            mPoints = new float[mBytes.length * 4];
		        }

		        mRect.set(0, 0, getWidth(), getHeight());

		        for (int i = 0; i < mBytes.length - 1; i++) {
		            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
		            mPoints[i * 4 + 1] = mRect.height() / 2
		                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
		            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
		            mPoints[i * 4 + 3] = mRect.height() / 2
		                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
		        }

		        canvas.drawLines(mPoints, mForePaint);
		    }
		}
}
