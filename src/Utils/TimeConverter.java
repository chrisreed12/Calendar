package Utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeConverter {
    public static Timestamp toUTC(Timestamp ts) throws ParseException {
        LocalDateTime ldt = ts.toLocalDateTime();
        ZonedDateTime zdt = ldt.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        return Timestamp.valueOf(utc.toLocalDateTime());
    }

    public static Timestamp fromUTC(Timestamp ts) throws ParseException {
        LocalDateTime ldt = ts.toLocalDateTime();
        ZonedDateTime utc = ldt.atZone(ZoneId.of("UTC"));
        ZonedDateTime zdt = utc.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        return Timestamp.valueOf(zdt.toLocalDateTime());
    }
}
