package com.rpi.ha.widget;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;

import com.rpi.ha.Conf;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class HKOweather {

	public static SyndFeed getRSS()
	{
		try {
			SyndFeedInput input = new SyndFeedInput();
			URL feedurl = new URL(Conf.hkoFeedURL);
			SyndFeed feed;
			feed = input.build(new XmlReader(feedurl));
			return feed;
		} catch (Exception e) {
			return null;
		}
	}

	public static BufferedImage getWeatherImage()
	{
		Document desc = getTempDescription();
		if (desc == null){
			return null;
		}
		Element img = desc.select("img").first();
		String url = img.absUrl("src");
		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(new URL(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bimg;
	}
	
	public static String getWeatherImageURL()
	{
		Document desc = getTempDescription();
		if (desc == null){
			return null;
		}
		Element img = desc.select("img").first();
		String url = img.absUrl("src");
		return url;
	}

	public static int getTemp(int locationState)
	{
		locationState--;
		Iterator<Element> ite = getTempTableCol();
		if (ite == null){
			return 5000;
		}
		for (int i = 0; i < locationState; i++)
		{
			ite.next();
		}
		int temp = 0;
		String text = ite.next().text();
		return temp;
	}

	public static List<SyndEntryImpl> getRSSEntries(){
		SyndFeed feed = getRSS();
		if (feed == null)
		{
			return null;
		}
		return feed.getEntries();
	}
	
	public static Document getTempDescription()
	{
		List<SyndEntryImpl> entries = getRSSEntries();
		if (entries == null)
		{
			return null;
		}
		String hkorss = entries.get(0).getDescription().getValue();
		return Jsoup.parse(hkorss);
	}
	
	public static Element getTempTable()
	{
		Document desc = getTempDescription();
		if (desc == null){
			return null;
		}
		return desc.select("table").first();
	}

	public static Iterator<Element> getTempTableCol()
	{
		Element table = getTempTable();
		if (table == null){
			return null;
		}
		return table.select("td[width=100]").iterator();
	}
}
