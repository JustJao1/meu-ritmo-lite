package br.com.joaovictor.meuritmolite;
import java.time.*;import java.util.*;
public final class Occurrence {
 public static boolean occurs(LocalDate start,String repeat,Set<Integer> days,LocalDate date){if(date.isBefore(start))return false;if("daily".equals(repeat))return true;if("weekly".equals(repeat))return days.isEmpty()?start.getDayOfWeek()==date.getDayOfWeek():days.contains(date.getDayOfWeek().getValue()%7);if("monthly".equals(repeat))return date.getDayOfMonth()==Math.min(start.getDayOfMonth(),date.lengthOfMonth());return start.equals(date);}
 public static long next(LocalDate start,LocalTime time,String repeat,Set<Integer> days,Set<String> skipped,long now,long last,ZoneId zone){LocalDate first=Instant.ofEpochMilli(now).atZone(zone).toLocalDate();if(start.isAfter(first))first=start;for(int n=0;n<400;n++){LocalDate date=first.plusDays(n);if(!occurs(start,repeat,days,date)||skipped.contains(date.toString()))continue;long at=date.atTime(time).atZone(zone).toInstant().toEpochMilli();if(at<=last)continue;if(at>=now)return at;if(at>now-60000)return now+2000;}return -1;}
}
