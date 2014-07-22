package com.liuxiong.earthquakereporter;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Handler;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HandshakeCompletedListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by liuxi_000 on 2014/7/16.
 */
public class EarthquakeListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    SimpleCursorAdapter adapter;
    private static final String TAG = "EARTHQUAKE";
    Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SimpleCursorAdapter( getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[]{ EarthquakeProvider.KEY_SUMMARY},
                new int[] { android.R.id.text1 }, 0);
        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshEarthquakes();
            }
        });
        thread.start();
    }

    void AddQuake(Quake quake) {
        ContentResolver cr = getActivity().getContentResolver();
        String w = EarthquakeProvider.KEY_DATE + " = " + quake.getDate().getTime();

        Cursor query = cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
        if(query.getCount()==0) {
            ContentValues values = new ContentValues();
            values.put(EarthquakeProvider.KEY_DATE, quake.getDate().getTime());
            values.put(EarthquakeProvider.KEY_DETAILS, quake.getDetails());
            values.put(EarthquakeProvider.KEY_SUMMARY, quake.toString());
            values.put(EarthquakeProvider.KEY_LOCATION_LAG, quake.getLocation().getLongitude());
            values.put(EarthquakeProvider.KEY_LOCATION_LAT, quake.getLocation().getLatitude());
            values.put(EarthquakeProvider.KEY_MAGNITUDE, quake.getMagnitude());
            values.put(EarthquakeProvider.KEY_LINK, quake.getLink());

            cr.insert(EarthquakeProvider.CONTENT_URI, values);
            getLoaderManager().restartLoader(0, null, this);
        }
        query.close();
    }

    public void refreshEarthquakes() {
        try {
            URL url = new URL(getString(R.string.quake_feed));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();

                System.out.println(in);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(in);
                Element docElement = document.getDocumentElement();
                NodeList nodeList = docElement.getElementsByTagName("entry");
                for(int i=0;i<nodeList.getLength();++i) {
                    Element entry = (Element)nodeList.item(i);
                    Element title = (Element)entry.getElementsByTagName("title").item(0);
                    Element g = (Element)entry.getElementsByTagName("georss:point").item(0);
                    Element when = (Element)entry.getElementsByTagName("updated").item(0);
                    Element link = (Element)entry.getElementsByTagName("link").item(0);

                    String details = title.getTextContent();
                    String linkString = link.getAttribute("href");
                    String point = g.getTextContent();
                    String dt = when.getTextContent();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = sdf.parse(dt);

                    String[] locationStr = point.split(" ");
                    Location loc = new Location("dummyGPS");
                    loc.setLatitude(Double.parseDouble(locationStr[0]));
                    loc.setLongitude(Double.parseDouble(locationStr[1]));

                    double magnitude = Double.parseDouble(details.split(" ")[1]);
                    final Quake quake = new Quake(date, details, loc, magnitude, linkString );

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AddQuake(quake);
                        }
                    });
                }
            }
        } catch(MalformedURLException ex) {
            Log.d(TAG, "Malformed");
        } catch (IOException ex) {
            Log.d(TAG, "IOException");
        } catch(ParserConfigurationException ex) {
            Log.d(TAG, "ParseConfigurationException");
        } catch(SAXException ex) {
            Log.d(TAG, "SAXException");
        } catch(ParseException ex) {
            Log.d(TAG, "ParseException");
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(getActivity(),
                EarthquakeProvider.CONTENT_URI, null, null, null, null);
        return loader;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
