/**
 * Copyright 2009 Joe LaPenna
 */

package com.joelapenna.foursquare.parsers;

import com.joelapenna.foursquare.Foursquare;
import com.joelapenna.foursquare.error.FoursquareError;
import com.joelapenna.foursquare.error.FoursquareParseException;
import com.joelapenna.foursquare.types.FoursquareType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public abstract class AbstractParser<T extends FoursquareType> implements Parser<T> {
    private static final String TAG = "AbstractParser";
    private static final boolean DEBUG = Foursquare.DEBUG;

    private static XmlPullParserFactory sFactory;
    static {
        try {
            sFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Could not create a factory");
        }
    }

    abstract protected T parseInner(final XmlPullParser parser) throws IOException,
            XmlPullParserException, FoursquareError, FoursquareParseException;

    /*
     * (non-Javadoc)
     * @see com.joelapenna.foursquare.parsers.Parser#parse(java.io.InputStream)
     */
    public final T parse(XmlPullParser parser) throws FoursquareParseException, FoursquareError {
        try {
            if (parser.getEventType() == XmlPullParser.START_DOCUMENT) {
                parser.nextTag();
            }
            return parseInner(parser);
        } catch (IOException e) {
            if (DEBUG) Log.d(TAG, "IOException", e);
            throw new FoursquareParseException(e.getMessage());
        } catch (XmlPullParserException e) {
            if (DEBUG) Log.d(TAG, "XmlPullParserException", e);
            throw new FoursquareParseException(e.getMessage());
        }
    }

    public static final XmlPullParser createXmlPullParser(InputStream is) {
        XmlPullParser parser;
        try {
            parser = sFactory.newPullParser();
            parser.setInput(is, null);
        } catch (XmlPullParserException e) {
            throw new IllegalArgumentException();
        }
        return parser;
    }

    public static void skipSubTree(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, null);
        int level = 1;
        while (level > 0) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.END_TAG) {
                --level;
            } else if (eventType == XmlPullParser.START_TAG) {
                ++level;
            }
        }
    }

}
