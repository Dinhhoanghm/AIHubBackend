package ongoing.backend.service.webScraping;

import com.aiv.datasource.IEDatasource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class WebDataSource implements IEDatasource {
  @Autowired
  private WebScrapingService webScrapingService;
  @Override
  public Map<String, Object> getData(Map<String, Object> map) {
    String url = (String) map.getOrDefault("urlConnection","");
    Integer tableIndex = Integer.valueOf(map.getOrDefault("tableIndex","").toString());
    Integer headerDepth = Integer.valueOf(map.getOrDefault("headerDepth","").toString());
    return webScrapingService.getTableData(url, tableIndex, headerDepth);
  }

  @Override
  public Map<String, Object> getMetaData(Map<String, Object> map) {
    String url = (String) map.getOrDefault("urlConnection","");
    Integer tableIndex = Integer.valueOf(map.getOrDefault("tableIndex","").toString());
    Integer headerDepth = Integer.valueOf(map.getOrDefault("headerDepth","").toString());
    return webScrapingService.getTableColumn(url, tableIndex, headerDepth);
  }

  @Override
  public Map<String, Object> getObjects(Map<String, Object> map) {
    String url = (String) map.getOrDefault("urlConnection","");
    return webScrapingService.getAllTablesInfo(url);
  }

  @Override
  public Map<String, Object> testConnection(Map<String, Object> map) {
    try {
      Map<String, Object> cart = new HashMap<String, Object>();

      cart.put("message", "Success");
      cart.put("connection", "Success");
      log.info("Successfully connected to database.");
      return cart;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
