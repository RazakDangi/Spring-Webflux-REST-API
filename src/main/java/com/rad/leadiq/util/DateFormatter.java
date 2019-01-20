package com.rad.leadiq.util;

import java.util.Date;
import java.util.TimeZone;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * This class is used for formatting/parsing date time according to ISO 8601. 
 * We allow any ISO 8601 compliant date to be used as input for our service, and will format all outgoing dates according to RFC 3339, which is a profile of ISO 8601
 * This class is thread safe.
 */
public final class DateFormatter {
   
    public static final String PATTERN_RFC_3339 = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";

    public static final DateTimeFormatter ISO_8601_DATE_PARSER = ISODateTimeFormat.dateTimeParser().withZoneUTC();

    // RFC 3339 is a profile of ISO 8601
    public static final DateTimeFormatter RFC_3339_FORMATTER = DateTimeFormat.forPattern(PATTERN_RFC_3339)
            .withZoneUTC();
    
    private DateFormatter() {
    }
    
    /**
     * Server will format the date time according to RFC 3339, http://tools.ietf.org/html/rfc3339#page-8
     * Atom syndication format requires date to be formatted with RFC 3339
     *  
     * According to RFC 3339, a colon will be used as delimiter in time zone offset <code>time-numoffset  = ("+" / "-") time-hour ":" time-minute</code>
     * @param date a Date instance
     * @return null if the date is null, otherwise a string compliant with RFC 3339 for the provided date 
     */
    public static String print(Date date) {
        return date == null ? null : RFC_3339_FORMATTER.print(date.getTime());
    }

      
 }
