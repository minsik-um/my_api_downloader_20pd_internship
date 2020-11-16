package um;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    /**
     * 작성자: 엄민식, dt2003979@dt20gw.hyosungitx.com
     */
    public static void main(String[] args) {
        example2();
    }

    /*
     * [예시 1]
     * API: https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15000947
     * 
     * 총 row 수는 456개, 한 요청에 한 페이지(10 row)를 지정해서 호출하는 구조.
     * 
     * for문으로 모든 페이지를 돌면서 호출 반복.
     * 
     * i = 46부터 데이터가 없으므로 exception이 발생하는게 정상임.
     */
    public static void example1() {
        String SERVICE_KEY = "X81j%2BTvf6SUFXbZ3SYoTretkd5c7Q7xz7jn9VIIzXyVZCpEh8bNNbEn4Zvkelg0W7E4COM9byuVm7gXLA14Ocw%3D%3D";
        String BASE_URL = "https://www.fact.or.kr/openapi/service/patentInfo/patentInfoList";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("pageNo", "");

        MyApiDownloader downloader = new MyApiDownloader(SERVICE_KEY);

        for (int i = 1; i <= 47; i++) {
            System.out.println(String.format("[%d] ...", i));  // exception 발생 시 어느 구간인지 확인하기 위한 표시점
            
            parameters.put("pageNo", Integer.toString(i));
            downloader.downloadApi(BASE_URL, parameters);
        }
    }

    /**
     * [예시 2]
     * API: https://www.fact.or.kr/openapi/service/farmMachine/farmMachineView2
     * 
     * 한 요청에서 rcpno(검정 번호 코드)에 대응되는 상세 정보를 불러오는 구조.
     * 
     * farmMachineList 라는 API 로 확인한 결과 RCPNO 갯수는 4550개임.
     * (개발계정 일일 트래픽 1000개를 가뿐히 넘으므로 운영계정 신청해야 함)
     * 
     * (DISTINCT를 사용한 SQL 구문을 이용하여)
     * wwwfactorkr_farmmachinelist 테이블에 있는 모든 RCPNO 를 목록으로 가져오고, (downloader.getDistinctValues)
     * 
     * 모든 RCPNO를 돌면서 전부 호출하여 결과를 저장.
     */
    public static void example2() {
        String SERVICE_KEY = "X81j%2BTvf6SUFXbZ3SYoTretkd5c7Q7xz7jn9VIIzXyVZCpEh8bNNbEn4Zvkelg0W7E4COM9byuVm7gXLA14Ocw%3D%3D";
        String BASE_URL = "https://www.fact.or.kr/openapi/service/farmMachine/farmMachineView2";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("RCPNO", "");

        MyApiDownloader downloader = new MyApiDownloader(SERVICE_KEY);
        List<String> codes = downloader.getDistinctValues("RCPNO", "wwwfactorkr_farmmachinelist");

        for (String code : codes) {
            System.out.println(String.format("[%s] ...", code));  // exception 발생 시 어느 구간인지 확인하기 위한 표시점
            
            parameters.put("RCPNO", code);
            downloader.downloadApi(BASE_URL, parameters);
        }
    }
}
