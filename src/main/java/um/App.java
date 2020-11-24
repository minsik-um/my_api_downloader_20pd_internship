package um;

import java.util.HashMap;
import java.util.Map;

public class App {

    /**
     * 예시에서 다루는 api: https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15000947
     * Service key랑 url은 제가 사용하는 건데 테스트 삼아 써도 됩니다.
     * 
     * pageNo 파라미터를 1부터 48까지 바꿔가며 요청하는 코드입니다. 
     * 자유롭게 반복문을 구성하여 요청하면 됩니다.
     * 
     * 작성자: 엄민식, dt2003979@dt20gw.hyosungitx.com
     */ 
    public static void main(String[] args) {
        String SERVICE_KEY = "X81j%2BTvf6SUFXbZ3SYoTretkd5c7Q7xz7jn9VIIzXyVZCpEh8bNNbEn4Zvkelg0W7E4COM9byuVm7gXLA14Ocw%3D%3D";
        String BASE_URL = "https://www.fact.or.kr/openapi/service/patentInfo/patentInfoList";
        String ITEM_TAG = "item";
    
        Map<String, String> parameters = new HashMap<>();
        MyApiDownloader downloader = new MyApiDownloader(SERVICE_KEY);
    
        for (int i = 1; i <= 48; i++) {
            System.out.println(String.format("[pageNo %d] ...", i)); 
            
            parameters.put("pageNo", Integer.toString(i));
            downloader.downloadApi(BASE_URL, parameters, ITEM_TAG);
        }
    }
}
