package br.ufpe.cin.dsoa.epcenter;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TestEventJoin implements UpdateListener {
	private EPServiceProvider epServiceProvider;
	private EPStatement stmt; 

	Long[] stockIds = { 1L, 2L, 3L };
	String[] stockNames = { "PT3", "VL2", "AS4"};
	String[] news = { "Aquisition...", "Selling..." };
	
	class TickEvent {
		private Long eventId;
		private Long stockId;
		private String stockName;
		private double cotation;
		
		public TickEvent(Long eventId, Long stockId, String stockName, double cotation) {
			super();
			this.eventId = eventId;
			this.stockId = stockId;
			this.stockName = stockName;
			this.cotation = cotation;
		}
		
		public Long getEventId() {
			return eventId;
		}

		public void setEventId(Long eventId) {
			this.eventId = eventId;
		}

		public Long getStockId() {
			return stockId;
		}
		public void setStockId(Long stockId) {
			this.stockId = stockId;
		}
		public String getStockName() {
			return stockName;
		}
		public void setStockName(String stockName) {
			this.stockName = stockName;
		}
		public double getCotation() {
			return cotation;
		}
		public void setCotation(double cotation) {
			this.cotation = cotation;
		}

		@Override
		public String toString() {
			return "TickEvent [eventId=" + eventId + ", stockId=" + stockId + ", stockName=" + stockName
					+ ", cotation=" + cotation + "]";
		}
		
	}
	
	class NewsEvent {
		private Long eventId;
		private Long stockId;
		private String news;
		
		public NewsEvent(Long eventId, Long stockId, String news) {
			super();
			this.eventId = eventId;
			this.stockId = stockId;
			this.news = news;
		}
		
		public Long getEventId() {
			return eventId;
		}

		public void setEventId(Long eventId) {
			this.eventId = eventId;
		}

		public Long getStockId() {
			return stockId;
		}
		public void setStockId(Long stockId) {
			this.stockId = stockId;
		}
		public String getNews() {
			return news;
		}
		public void setNews(String news) {
			this.news = news;
		}

		@Override
		public String toString() {
			return "NewsEvent [eventId=" + eventId + ", stockId=" + stockId + ", news=" + news + "]";
		}
		
		
	}
	
	@Before
	public void setUp() {
		Configuration config = new Configuration();
		config.addEventType(TickEvent.class);
		config.addEventType(NewsEvent.class);
		epServiceProvider = EPServiceProviderManager.getProvider("EngineInstance", config);
		String epl = "select TE.eventId, NE.eventId, TE.stockId, cotation, news " +
				"from  TickEvent.win:time(1) as TE, NewsEvent.win:time(1) as NE " +
				"where TE.stockId = NE.stockId";
		stmt = this.epServiceProvider.getEPAdministrator().createEPL(epl);
		stmt.addListener(this);
	}
	
	@Test
	public void sendEvents() {
		String stockName;
		String newl;
		long stockId;
		double cotation;
		Random random = new Random();
		Long id = 1L;
		for (int i = 0; i < 10; i++) {
			System.out.println("==== i: " + i + " ====");
			stockId = stockIds[random.nextInt(stockIds.length)];
			stockName = stockNames[random.nextInt(stockNames.length)];
			newl  = news[random.nextInt(news.length)];
			cotation = i;
			stockId = 2;
			NewsEvent ne = new NewsEvent(id++, stockId, newl);
			System.out.println(ne);
			this.epServiceProvider.getEPRuntime().sendEvent(ne);
			
			TickEvent te = new TickEvent(id++, stockId, stockName, cotation);
			System.out.println(te);
			this.epServiceProvider.getEPRuntime().sendEvent(te);
			
			ne = new NewsEvent(id++, stockId, newl);
			System.out.println(ne);
			this.epServiceProvider.getEPRuntime().sendEvent(ne);
			
			te = new TickEvent(id++, stockId, stockName, cotation);
			System.out.println(te);
			this.epServiceProvider.getEPRuntime().sendEvent(te);
			
			
			te = new TickEvent(id++, stockId, stockName, cotation);
			System.out.println(te);
			this.epServiceProvider.getEPRuntime().sendEvent(te);

			/*System.out.println("----------------------------------------------------------------------------------------------------------");
			Iterator itr = stmt.iterator();
			while(itr.hasNext()) {
				System.out.println(((EventBean)itr.next()).getUnderlying());
			}
			System.out.println("----------------------------------------------------------------------------------------------------------");*/
		}
	}

	@After
	public void tearDown() {
		this.epServiceProvider.destroy();
	}

	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			System.out.println("New Events: ");
			for (EventBean event : newEvents) {
				System.out.println(event.getUnderlying());
				/*Map<String, Object> propertyMap = (Map<String,Object>)event.getUnderlying();
				for (String key : propertyMap.keySet()) {
					System.out.println("key: " + key);
					System.out.println("value: " + propertyMap.get(key));
				}*/
			}
		}
		
		if (oldEvents != null) {
			System.out.println("Old Events: ");
			for (EventBean event : oldEvents) {
				System.out.println(event.getUnderlying());
			}
		}
	}
}
