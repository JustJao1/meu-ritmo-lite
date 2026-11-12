package br.com.joaovictor.meuritmolite;
import android.app.*;import android.content.*;import android.os.*;
public class AlarmReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent in){String action=in.getAction(),id=in.getStringExtra("id");if(id==null)return;String base=Scheduler.base(id),title=in.getStringExtra("title");boolean sound=in.getBooleanExtra("sound",true);if(!Scheduler.exists(c,base))return;
 if("STOP".equals(action)||"SNOOZE".equals(action)){RingingService.dismissIfRunning(base);((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(RingingService.notificationId(c,base));Scheduler.dismissed(c,id,"SNOOZE".equals(action),title==null?"Meu Ritmo Lite":title,sound);return;}
 if(!"FIRE".equals(action))return;long at=in.getLongExtra("at",0);if(at<=0||System.currentTimeMillis()-at>15*60000)return;if(id.endsWith("#snooze")){Scheduler.prefs(c).edit().remove("snooze:"+base).apply();}else{if(at<=Scheduler.prefs(c).getLong("last:"+base,0))return;Scheduler.prefs(c).edit().putLong("last:"+base,at).commit();}Scheduler.reschedule(c);if(!Scheduler.notifications(c))return;
 if(sound){Intent service=new Intent(c,RingingService.class).setAction("RING").putExtra("id",base).putExtra("title",title);try{c.startForegroundService(service);}catch(Exception ignored){((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).notify(RingingService.notificationId(c,base),RingingService.notification(c,base,title,false,"Não foi possível iniciar o som. Verifique as permissões."));}}
 else ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).notify(RingingService.notificationId(c,base),RingingService.notification(c,base,title,false,"Lembrete do Meu Ritmo Lite"));
 }
}
