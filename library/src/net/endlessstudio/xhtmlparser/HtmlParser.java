package net.endlessstudio.xhtmlparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.endlessstudio.xhtmlparser.nod.BrNod;
import net.endlessstudio.xhtmlparser.nod.ElementNod;
import net.endlessstudio.xhtmlparser.nod.FontNod;
import net.endlessstudio.xhtmlparser.nod.ImageNod;
import net.endlessstudio.xhtmlparser.nod.Nod;
import net.endlessstudio.xhtmlparser.nod.RootNod;
import net.endlessstudio.xhtmlparser.nod.StrikeNode;
import net.endlessstudio.xhtmlparser.nod.TextNod;
import net.endlessstudio.xhtmlparser.nod.UnderlineNod;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.Spannable;
import android.text.Html.ImageGetter;
import android.text.SpannableString;

/**
 * font (size, color)
 * strike
 * br
 * img
 * @author John
 *
 */
public class HtmlParser {
	private ImageGetter mImageGetter;
	private SpannableString mResult;
	
	public class XHtmlHandler extends DefaultHandler{
		private Stack<Nod> mElementStack;
		
		@Override
		public void startDocument() throws SAXException {
			mElementStack = new Stack<Nod>();
			super.startDocument();
		}
		
		@Override
		public void endDocument() throws SAXException {
			Nod thisNod;
			Nod childNod = null;
			while(!mElementStack.isEmpty()){
				thisNod = mElementStack.pop();
				if(childNod == null){
					childNod = thisNod;
				}
				else{
					childNod = thisNod.execute(childNod);
				}
			}
			
			mResult = ((TextNod)childNod).getValue();
			
			super.endDocument();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("uri[%s] local name[%s] q name[%s]", uri, localName, qName)).append("\r\n");
			for(int i = 0; i < attributes.getLength(); i ++){
				String name = attributes.getLocalName(i);
				String value = attributes.getValue(i);
				String type = attributes.getType(i);
				sb.append(String.format("attribute[%s]: name[%s] type[%s] value[%s]", i, name, type, value)).append("\r\n");
			}
			ElementNod nod = createNod(localName, attributes);
			mElementStack.push(nod);
			
			super.startElement(uri, localName, qName, attributes);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			Nod thisNod;
			Nod childNod = null;
			while(!(thisNod = mElementStack.pop()).getName().equals(localName)){
				if(childNod == null){
					childNod = thisNod;
				}
				else{
					childNod = thisNod.execute(childNod);
				}
			}
			
			mElementStack.push(thisNod.execute(childNod));
			
			super.endElement(uri, localName, qName);
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			TextNod text = new TextNod(new String(ch, start, length));
			mElementStack.push(text);
			
			super.characters(ch, start, length);
		}
	}
	
	
	public HtmlParser(ImageGetter getter) {
		this.mImageGetter = getter;
	}
	
	public Spannable parseText(String text){
		if(text.indexOf("<xhtml>") == -1){
			text = new StringBuilder().append("<xhtml>").append(text).append("</xhtml>").toString();
		}
		
		return parseXHtml(text);
	}
	
	public Spannable parseXHtml(String xhtml){
		try{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			ByteArrayInputStream bais = new ByteArrayInputStream(xhtml.getBytes());
			XHtmlHandler handler = new XHtmlHandler();
			parser.parse(bais, handler);
			return mResult;
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
			//do nothing
		}
		catch(IOException e){
			e.printStackTrace();
			//do nothong
		}
		catch(SAXException e){
			e.printStackTrace();
			//do nothing
		}
		
		return null;
	}
	
	public ElementNod createNod(String name, Attributes attributes) throws UnknownTagException {
		if ("xhtml".equals(name)){
			return new RootNod(name);
		}
		else if ("font".equals(name) || "f".equals(name)) {
			return new FontNod(name, attributes);
		}
		else if ("strike".equals(name) || "s".equals(name)) {
			return new StrikeNode(name);
		}
		else if ("br".equals(name)) {
			return new BrNod(name);
		}
		else if ("img".equals(name) || "i".equals(name)) {
			return new ImageNod(name, attributes, mImageGetter);
		}
		else if ("underline".equals(name) || "u".equals(name)) {
			return new UnderlineNod(name);
		}

		throw new UnknownTagException(name);
	}
	
	public SpannableString getResult(){
		return mResult;
	}
}
