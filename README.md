# My API Downloader
2020 공공데이터 청년인턴십 - API 품질진단 업무에서 `api checker` 프로그램의 기능을 비슷하게 구현했습니다. `api checker`와 달리 파라미터 수정을 코드로 자동화할 수 있습니다.

### 문의
- Java 어떻게 깔아요? 실행 어떻게 해요? Maven이 뭐에요?: 죄송합니다...
- 그 외: dt2003979@dt20gw.hyosungitx.com 여기로 문의 주시면 근무 시간에 도와드리겠습니다!

### 대상
- API 품질 검증 업무를 수행하는 공공데이터 청년 인턴
- Java 가능자

### 필요성
1. `api checker`를 이용하여 본 업무를 하려면 가능한 input parameter를 일일이 수정해서 실행해야 함
2. 위의 수정횟수는 한 두번이 아니라 수십번, 수백번도 필요할 수 있음
3. 간단한 반복문 코딩으로 parameter 수정을 하면서 요청하면 손목 피로와 시간을 줄일 수 있음!

### 플랫폼
Java Maven Project(의존성 라이브러리 확인 해주세요)

### 구현 기능
REST API(xml 결과 반환) 요청한 결과값을 MariaDB에 저장

### 사용방법
`MyApiDownloader.java`에 필요한 기능을 구현했습니다. `App.java`에서 해당 객체를 만들어 사용하면 됩니다. `main` 메소드는 하나의 api 전체를 받는 코드입니다.

`App.java`에 넣은 `main` 메소드에서 아래 4가지 부분을 자신의 api에 맞게 수정하고 실행합니다.

- SERVICE_KEY: 인증키(서비스키)
- BASE_URL: 오퍼레이션 url
- ITEM_TAG: xml 결과값에서 각 row 데이터를 감싸는 태그 이름()
- parameters: 쿼리 파라미터

```java
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
```

#### ITEM_TAG 이해를 돕기 위한 xml 결과 예시
row 데이터를 감싸는 태그 이름이 `item`입니다. `ITEM_TAG`로 지정하는 값도 동일하게 `item`으로 설정한 것입니다.

```xml
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
      <!-- 이하 생략 -->
    </items>
    <numOfRows>5</numOfRows>
    <pageNo>5</pageNo>
    <totalCount>718</totalCount>
  </body>
</response>
```

#### 실행 결과 예시
예시 코드는 파라미터 단위로 실행 결과를 확인합니다. `[파라미터 값]...` 이후 아무것도 없으면 처리를 성공한 것이고, exception이 하나라도 뜨면 실패한 요청입니다. 실패한 요청만 따로 보면서 원래 되어야하는 요청인지 확인합니다.

```
[pageNo 1] ...
[pageNo 2] ...
[pageNo 3] ...
...
[pageNo 45] ...
[pageNo 46] ...
[pageNo 47] ...
java.lang.ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
        at um.MyApiDownloader.makeSqlCreateTbl(MyApiDownloader.java:196)
        at um.MyApiDownloader.makeSqlInsert(MyApiDownloader.java:216)
        at um.MyApiDownloader.downloadApi(MyApiDownloader.java:43)
        at um.App.main(App.java:25)
[pageNo 48] ...
java.lang.ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
        at um.MyApiDownloader.makeSqlCreateTbl(MyApiDownloader.java:196)
        at um.MyApiDownloader.makeSqlInsert(MyApiDownloader.java:216)
        at um.MyApiDownloader.downloadApi(MyApiDownloader.java:43)
        at um.App.main(App.java:25)
```

- 성공: 853, 854, 855
- 실패: 856, 857

`Index 0` 예외는 받아온 row가 없을 때 발생합니다. 총 209개에서 한 페이지에 10개씩 `pageNo(페이지번호)`로 지정해서 받아오는 api라면, `pageNo`가 21일 때 210~219번째 row를 요청하므로 당연히 `Index 0` 에러가 발생합니다.