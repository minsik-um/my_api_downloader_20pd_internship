# My API Downloader
2020 공공데이터 청년인턴십 - API 품질진단 업무에서 `api checker` 프로그램의 기능을 비슷하게 구현했습니다. `api checker`와 달리 파라미터 수정을 코드로 자동화할 수 있습니다.

### 대상
- API 품질 검증 업무를 수행하는 공공데이터 청년 인턴
- Java 가능자

### 필요성
1. `api checker`를 이용하여 본 업무를 하려면 가능한 input parameter를 일일이 수정해서 실행해야 함
2. 위의 수정횟수는 한 두번이 아니라 수십번, 수백번도 필요할 수 있음
3. 간단한 반복문 코딩으로 parameter 수정을 하면서 요청하면 손목 피로와 시간을 줄일 수 있음!

### 플랫폼
Java Maven Project

### 구현 기능
REST API(xml 결과 반환) 요청한 결과값을 MariaDB에 저장

### 사용방법
`MyApiDownloader.java`에 필요한 기능을 구현했습니다. `App.java`에서 해당 객체를 만들어 사용하면 됩니다.

1. Java 개발 환경을 구성합니다. `Java 11`이 아니라면 `pom.xml`을 개발환경 버전에 맞게 수정해주면 됩니다.
2. Maven Project를 Import 합니다. Eclipse도 좋고 VSC도 좋습니다. 전 VSC에서 개발하고 업무를 수행했습니다.
3. `App.java`에 넣은 `example1`, `example2` 예시를 참고하여 각자 상황에 맞게 로직을 짜서 실행합니다.

#### 실행 코드
- SERVICE_KEY: 인증키(서비스키)
- BASE_URL: 오퍼레이션 url
- ITEM_TAG: xml 결과값에서 각 row 데이터를 감싸는 태그 이름

```java
public static void example1() {
    String SERVICE_KEY = "X81j%2BTvf6SUFXbZ3SYoTretkd5c7Q7xz7jn9VIIzXyVZCpEh8bNNbEn4Zvkelg0W7E4COM9byuVm7gXLA14Ocw%3D%3D";
    String BASE_URL = "https://www.fact.or.kr/openapi/service/farmTech/farmTechView";
    String ITEM_TAG = "item";

    Map<String, String> parameters = new HashMap<>();
    parameters.put("seq", "");

    MyApiDownloader downloader = new MyApiDownloader(SERVICE_KEY);

    for (int i = 1; i <= 46; i++) {
        System.out.println(String.format("[%d] ...", i));  // exception 발생 시 어느 구간인지 확인하기 위한 표시점

        parameters.put("seq", Integer.toString(i));
        downloader.downloadApi(BASE_URL, parameters, ITEM_TAG);
    }
}
```

#### ITEM_TAG 이해를 돕기 위한 결과 예시
```
<response>
  <header>
    <resultCode>00</resultCode>
    <resultMsg>NORMAL SERVICE.</resultMsg>
  </header>
  <body>
    <items>
      <item>
        <regdate>2014.10.16</regdate>
        <regname>엄인용</regname>
        <seq>701</seq>
        <subject>[FACT]마늘의 건강기능식품 고시형 원료 등록에 따른 증분수요 추정 보고서</subject>
        <viewCount>1368</viewCount>
      </item>
      <item>
        <regdate>2014.09.26</regdate>
        <regname>권윤구</regname>
        <seq>700</seq>
        <subject>[FACT]농·식품분야 최신특허 동향(‘14.5~‘14.7월)</subject>
        <viewCount>1383</viewCount>
      </item>
      <item>
        <regdate>2014.09.25</regdate>
        <regname>권윤구</regname>
        <seq>699</seq>
        <subject>[FACT]국내 농식품분야 융복합 특허기술(‘14.5~14.7월 )</subject>
        <viewCount>1294</viewCount>
      </item>
      <item>
        <regdate>2014.09.17</regdate>
        <regname>권윤구</regname>
        <seq>698</seq>
        <subject>[전문가기고]축산업의 6차 산업화 필요성과 추진방향</subject>
        <viewCount>1331</viewCount>
      </item>
      <item>
        <regdate>2014.09.17</regdate>
        <regname>권윤구</regname>
        <seq>697</seq>
        <subject>[전문가기고]6차 산업화의 해외 사례 및 시사점</subject>
        <viewCount>1480</viewCount>
      </item>
    </items>
    <numOfRows>5</numOfRows>
    <pageNo>5</pageNo>
    <totalCount>718</totalCount>
  </body>
</response>
```

#### 실행 결과
- 성공: 853, 854, 855, 857, 858, 859, 861, 862
- 실패: 856, 860 (exception이 하나라도 뜨면 실패한 요청입니다)

```
[853] ...
[854] ...
[855] ...
[856] ...
java.lang.ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
        at um.MyApiDownloader.makeSqlCreateTbl(MyApiDownloader.java:193)
        at um.MyApiDownloader.makeSqlInsert(MyApiDownloader.java:207)
        at um.MyApiDownloader.downloadApi(MyApiDownloader.java:43)
        at um.App.example1(App.java:38)
        at um.App.main(App.java:13)
[857] ...
[858] ...
[859] ...
[860] ...
java.lang.ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
        at um.MyApiDownloader.makeSqlCreateTbl(MyApiDownloader.java:193)
        at um.MyApiDownloader.makeSqlInsert(MyApiDownloader.java:207)
        at um.MyApiDownloader.downloadApi(MyApiDownloader.java:43)
        at um.App.example1(App.java:38)
        at um.App.main(App.java:13)
[861] ...
[862] ...
```

### 문의
- Java 어떻게 깔아요? 실행 어떻게 해요? Maven이 뭐에요?: 죄송합니다...
- 그 외: dt2003979@dt20gw.hyosungitx.com 여기로 문의 주시면 근무 시간에 도와드리겠습니다!
