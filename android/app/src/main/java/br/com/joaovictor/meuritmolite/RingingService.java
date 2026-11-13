package br.com.joaovictor.meuritmolite;
import android.app.*;import android.content.*;import android.media.*;import android.os.*;import java.util.*;
public class RingingService extends Service {
 private static volatile RingingService instance;private final LinkedHashMap<String,String> active=new LinkedHashMap<>();private final Handler handler=new Handler(Looper.getMainLooper());private AudioTrack alarmTrack;private android.os.Vibrator vibrator;private PowerManager.WakeLock wake;private String playing;
 static org.json.JSONArray activeSnapshot(){org.json.JSONArray out=new org.json.JSONArray();RingingService r=instance;if(r!=null)synchronized(r){for(java.util.Map.Entry<String,String> e:r.active.entrySet())try{out.put(new org.json.JSONObject().put("id",e.getKey()).put("title",e.getValue()));}catch(Exception ignored){}}return out;}
 static synchronized int notificationId(Context c,String id){android.content.SharedPreferences p=Scheduler.prefs(c);String k="notification:"+id;int n=p.getInt(k,0);if(n==0){n=p.getInt("nextNotification",1000)+1;p.edit().putInt(k,n).putInt("nextNotification",n).commit();}return n;}
 static PendingIntent action(Context c,String id,String title,boolean sound,String action){return Scheduler.pending(c,id,0,title,sound,action);}
 static Notification notification(Context c,String id,String title,boolean ringing,String body){Scheduler.channels(c);PendingIntent open=PendingIntent.getActivity(c,notificationId(c,id),new Intent(c,MainActivity.class).setAction("OPEN_ALARMS").setData(android.net.Uri.parse("ritmo://open/"+android.net.Uri.encode(id))),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);return new Notification.Builder(c,ringing?Scheduler.CHANNEL:Scheduler.REMINDERS).setSmallIcon(R.drawable.ic_notification).setContentTitle(title==null?"Meu Ritmo Lite":title).setContentText(body).setContentIntent(open).setCategory(Notification.CATEGORY_ALARM).setVisibility(Notification.VISIBILITY_PRIVATE).setOngoing(ringing).setOnlyAlertOnce(ringing).addAction(new Notification.Action.Builder(null,ringing?"Parar":"Concluir",action(c,id,title,Scheduler.soundFor(c,id),"STOP")).build()).addAction(new Notification.Action.Builder(null,"Adiar 10 min",action(c,id,title,Scheduler.soundFor(c,id),"SNOOZE")).build()).build();}
 public static void dismissIfRunning(String id){RingingService r=instance;if(r!=null)r.handler.post(()->r.remove(id,false));}
 @Override public void onCreate(){super.onCreate();instance=this;Scheduler.channels(this);wake=((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MeuRitmo:alarme");wake.acquire(180000);vibrator=(android.os.Vibrator)getSystemService(VIBRATOR_SERVICE);}
 @Override public synchronized int onStartCommand(Intent in,int flags,int startId){if(in==null){stopSelf();return START_NOT_STICKY;}String id=in.getStringExtra("id"),title=in.getStringExtra("title");if(id==null||!Scheduler.exists(this,id)){stopSelf();return START_NOT_STICKY;}active.put(id,title==null?"Meu Ritmo Lite":title);startForeground(notificationId(this,id),notification(this,id,title,true,"Alarme tocando · Parar ou adiar abaixo"));((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(notificationId(this,id),notification(this,id,title,true,"Alarme tocando · Parar ou adiar abaixo"));if(playing==null)playNext();final String item=id;handler.postDelayed(()->remove(item,true),120000);return START_NOT_STICKY;}
 private synchronized void playNext(){stopAudio();if(active.isEmpty())return;playing=active.keySet().iterator().next();try{
  final int rate=22050, seconds=2, samples=rate*seconds;
  short[] pcm=new short[samples];
  for(int i=0;i<samples;i++){
   double t=(double)i/rate, phase=t%0.72;
   double envelope=phase<0.46?Math.min(1.0,phase/0.012)*Math.min(1.0,(0.46-phase)/0.045):0.0;
   double hz=phase<0.23?880.0:1175.0;
   double tone=Math.sin(2*Math.PI*hz*t)*0.78+Math.sin(2*Math.PI*hz*2*t)*0.17;
   pcm[i]=(short)(Math.max(-1,Math.min(1,tone*envelope*0.78))*Short.MAX_VALUE);
  }
  alarmTrack=new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(rate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()).setTransferMode(AudioTrack.MODE_STATIC).setBufferSizeInBytes(pcm.length*2).build();
  alarmTrack.write(pcm,0,pcm.length);alarmTrack.setLoopPoints(0,pcm.length,-1);alarmTrack.play();
  if(vibrator!=null)vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,650,180,650,400},0));
 }catch(Exception ignored){} }
 private synchronized void remove(String id,boolean timedOut){if(!active.containsKey(id))return;String title=active.remove(id);NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);if(id.equals(playing)){stopAudio();playing=null;}if(timedOut)nm.notify(notificationId(this,id),notification(this,id,title,false,"O som encerrou após 2 minutos. Você pode adiar este alarme."));else nm.cancel(notificationId(this,id));if(active.isEmpty()){stopForeground(STOP_FOREGROUND_DETACH);stopSelf();}else{String next=active.keySet().iterator().next();startForeground(notificationId(this,next),notification(this,next,active.get(next),true,"Alarme tocando · Parar ou adiar abaixo"));if(playing==null)playNext();}}
 private void stopAudio(){if(alarmTrack!=null){try{alarmTrack.pause();alarmTrack.flush();alarmTrack.release();}catch(Exception ignored){}alarmTrack=null;}if(vibrator!=null)vibrator.cancel();}
 @Override public void onDestroy(){stopAudio();handler.removeCallbacksAndMessages(null);if(wake!=null&&wake.isHeld())wake.release();instance=null;super.onDestroy();}
 @Override public android.os.IBinder onBind(Intent i){return null;}
}
