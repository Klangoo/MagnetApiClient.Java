package com.klangoo;

/**
 * News Agency Sample Code
 *
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class App 
{
    private String ENDPOINT_URI = "https://magnetapi.klangoo.com/NewsAgencyService.svc";
	private String CALK = "";// use your own CALK
	private String SECRET_KEY = "";// use your own Secret Key
	
	private MagnetAPIClient _magnetAPIClient;
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy Z");
	
	private Calendar calendar = GregorianCalendar.getInstance();
	
	public static void main(String[] args) throws Exception {
		new App();
	}
	
	private App() throws Exception{
		_magnetAPIClient = new MagnetAPIClient(ENDPOINT_URI, CALK, SECRET_KEY);
	
		String articleUID = "2014/04/28/mariano-pavone-descarto-una-revancha-ante-leon";
		AddArticle(articleUID);
		GetArticle(articleUID);
		GetArticleSummary(articleUID);
		GetArticleKeyTopics(articleUID);
		GetArticleEntities(articleUID);
		GetArticleCategories(articleUID);
		GetRelatedArticles(articleUID);
		GetEntityRelatedArticles("3477018888");
		UpdateArticle(articleUID);
		GetArticle(articleUID);
		DeleteArticle(articleUID);
		ShowIndex();
	}
	
	public void AddArticle(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("text", "Mariano Pavone descartó una revancha ante León" + 
				"Con el antecedente de una eliminación a manos del León dirigido por Gustavo Matosas, además de haber padecido una expulsión en el juego de vuelta, Mariano Pavone no quiere que este nuevo duelo de Liguilla ante La Fiera sea de revancha.\n\n" +
"\"Más allá de la Eliminatoria de Cuartos de Final que habíamos conseguido un 2-1 en casa y 3-0 en León, creo que es diferente, más que revancha queremos seguir avanzando, queremos dejar viva la ilusión y para eso tenemos que dejar en el camino un rival difícil como León\", expresó.\n\n" +
				"Calificó como un hecho aislado la expulsión que recibió en ese partido celebrado en el Estadio León tras cometerle una falta al defensa Ignacio González. \"Es otro momento y sí tenemos que sacar un buen resultado en León porque es peligroso de local como de visitante\", advirtió.\n\n" +
				"El delantero, autor del gol que le aseguró a La Máquina el título de Concachampions, señaló que tanto el equipo como la afición no pierden la ansiedad de ganar un título de Liga después de 17 años de no conseguirlo.\n\n" +
				"\"Sabemos la ansiedad por cortar la sequía de títulos, la tenemos nosotros, la tienen los aficionados pero sabemos que hay que ir paso a paso, Fue muy bueno el título que ganamos de CONCACAF para demostrar que se pueden ganar las Finales\", expresó.\n\n" +
				"Descartó además que el horario de las 22 horas para el juego de ida afectará más a la afición que saldrá tarde del Estadio León que a los futbolistas que saltarán a la cancha.");
		request.put("title", "Mariano Pavone descartó una revancha ante León");
		
		calendar.set(2014, Calendar.APRIL, 28);
		Date insertDate = calendar.getTime();
		request.put("insertDate", simpleDateFormat.format(insertDate)); // article date
		request.put("url", "http://www.mediotiempo.com/futbol/mexico/noticias/2014/04/28/mariano-pavone-descarto-una-revancha-ante-leon");
		request.put("articleUID", articleUID);
		request.put("source", "mediotiempo.com");
		request.put("language", "es"); // Spanish
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("AddArticle", request, "POST");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("AddArticle:");
				System.out.println(response);
				WriteToFile(response, "AddArticle.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void UpdateArticle(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("articleUID", articleUID);
		request.put("text", "La modernidad nos tiene atados a nuestro smartphone: es común voltear a cualquier mesa en un restaurante o en alguna reunión familiar y ver que existe al menos una persona con la vista fija en su celular.\n\n" +
			"Y más allá de ser una costumbre socialmente aceptada o no, el dilema existe porque se crea un ambiente de soledad compartida con las personas que realizan esta actividad y que un par de emprendedores de Nueva York desean aminorar con su juego \"Game of Phones\".\n\n" +
				"Este título, que terminó apenas su fase de financiación en Kickstarter, es un juego de cartas para entretenerse entre amigos y que instruye a los participantes a buscar ciertas cosas en sus smartphones para ganar.");
		
		calendar.set(2014, Calendar.APRIL, 29);
		Date updateDate  = calendar.getTime();
		request.put("updateDate ", simpleDateFormat.format(updateDate )); // article date
		request.put("language", "es"); // Spanish
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("UpdateArticle", request, "POST");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("UpdateArticle:");
				System.out.println(response);
				WriteToFile(response, "UpdateArticle.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void DeleteArticle(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("articleUID", articleUID);
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("DeleteArticle", request, "POST");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("DeleteArticle:");
				System.out.println(response);
				WriteToFile(response, "DeleteArticle.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void GetArticle(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("articleUID", articleUID);
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("GetArticle", request, "GET");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("GetArticle:");
				System.out.println(response);
				WriteToFile(response, "GetArticle.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void GetArticleSummary(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("articleUID", articleUID);
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("GetArticleSummary", request, "GET");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("GetArticleSummary:");
				System.out.println(response);
				WriteToFile(response, "GetArticleSummary.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void GetArticleKeyTopics(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("articleUID", articleUID);
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("GetArticleKeyTopics", request, "GET");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("GetArticleKeyTopics:");
				System.out.println(response);
				WriteToFile(response, "GetArticleKeyTopics.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void GetArticleEntities(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("articleUID", articleUID);
		request.put("group", "1"); // group by type
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("GetArticleEntities", request, "GET");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("GetArticleEntities:");
				System.out.println(response);
				WriteToFile(response, "GetArticleEntities.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void GetArticleCategories(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("articleUID", articleUID);
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("GetArticleCategories", request, "GET");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("GetArticleCategories:");
				System.out.println(response);
				WriteToFile(response, "GetArticleCategories.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void GetRelatedArticles(String articleUID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("articleUID", articleUID);
		request.put("showDetails", "2");
		request.put("orderBy", "0"); // order by relevancy
		request.put("maxReturnRows", "10");
		request.put("format", "xml");
		
		calendar.set(2014, Calendar.DECEMBER, 1);
		Date filterDate = calendar.getTime();
		request.put("filterDate", simpleDateFormat.format(filterDate));
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("GetRelatedArticles", request, "GET");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("GetRelatedArticles:");
				System.out.println(response);
				WriteToFile(response, "GetRelatedArticles.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void GetEntityRelatedArticles(String entityID){
		Map<String, String> request = new HashMap<String, String>();
		request.put("entityID", entityID);
		request.put("category", "Sports");
		request.put("showDetails", "2");
		request.put("orderBy", "0"); // order by relevancy
		request.put("maxReturnRows", "10");
		request.put("format", "xml");
		
		calendar.set(2014, Calendar.FEBRUARY, 1);
		Date filterDate = calendar.getTime();
		request.put("filterDate", simpleDateFormat.format(filterDate));
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("GetEntityRelatedArticles", request, "GET");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("GetEntityRelatedArticles:");
				System.out.println(response);
				WriteToFile(response, "GetEntityRelatedArticles.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	public void ShowIndex(){
		Map<String, String> request = new HashMap<String, String>();
		request.put("page", "0");
		request.put("orderByDate", "true");
		request.put("format", "xml");
		
		try
		{
			String response = _magnetAPIClient.CallWebMethod("ShowIndex", request, "GET");
			Document doc = ParseXmlDocument(response);
			if (GetApiResponseStatus(doc).equals("OK")){
				System.out.println("ShowIndex:");
				System.out.println(response);
				WriteToFile(response, "ShowIndex.xml");
			}
			else {
				// ERROR
				HandleApiError(doc);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
	
	private String GetApiResponseStatus(Document doc){
		Element rootElement = doc.getDocumentElement();
		Node statusNode = rootElement.getElementsByTagName("status").item(0);
		return statusNode.getTextContent();
	}
	
	private void HandleApiError(Document doc){
		Element rootElement = doc.getDocumentElement();
		Element errorElement = (Element)rootElement.getElementsByTagName("error").item(0);
		Node errorNoNode = errorElement.getElementsByTagName("errorNo").item(0);
		int errorNo = Integer.parseInt(errorNoNode.getTextContent());
		String errorMessage = "";
		if (errorElement.getElementsByTagName("errorMessage").getLength() > 0){
			errorMessage = errorElement.getElementsByTagName("errorMessage").item(0).getTextContent();
		}
		System.out.println("Error occured -- errorNo: " + errorNo + " -- errorMessage: " + errorMessage);
	}
	
	private Document ParseXmlDocument(String xml) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(new InputSource(new StringReader(xml)));
	}

	private void WriteToFile(String response, String filename) {
		try
		{
			FileWriter fWriter = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fWriter);
			out.write(response);
			out.close();
		}
		catch(Exception ex) {
			System.out.println("Exception occured: " + ex.getMessage());
		}
	}
}
