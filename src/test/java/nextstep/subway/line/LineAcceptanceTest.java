package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.support.ApiSupporter;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //Given
        Line line = new Line("1호선", "blue");

        //When
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        //Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("id")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(line.getName()),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo(line.getColor()),
                () -> assertThat(response.jsonPath().getString("createdDate")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("modifiedDate")).isNotNull()
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_목록_조회_요청

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //Given
        ExtractableResponse<Response> createdResponse = ApiSupporter.callCreatedApi("1호선", "blue");
        LineResponse createdLine = createdResponse.jsonPath().getObject(".", LineResponse.class);

        //When
        String uri = createdResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                    .get(uri)
                .then().log().all()
                .extract();
        LineResponse expectedLine = createdResponse.jsonPath().getObject(".", LineResponse.class);

        //Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(expectedLine.getId()).isEqualTo(createdLine.getId()),
                () -> assertThat(expectedLine.getName()).isEqualTo(createdLine.getName()),
                () -> assertThat(expectedLine.getColor()).isEqualTo(createdLine.getColor()),
                () -> assertThat(expectedLine.getModifiedDate()).isEqualTo(createdLine.getModifiedDate()),
                () -> assertThat(expectedLine.getCreatedDate()).isEqualTo(createdLine.getCreatedDate()),
                () -> assertThat(response.jsonPath().getString("stations")).isNotNull()
        );
    }

    @DisplayName("등록되지 않는 지하철 노선을 조회한다.")
    @Test
    void notFoundLine() {
        //Given
        ExtractableResponse<Response> createdResponse = ApiSupporter.callCreatedApi("1호선", "blue");
        LineResponse createdLine = createdResponse.jsonPath().getObject(".", LineResponse.class);

        //When
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/lines/"+createdLine.getId()+1)
                .then().log().all()
                .extract();

        //Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );

    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //Given
        String station = "1호선";
        String color = "blue";

        ExtractableResponse<Response> createdResponse = ApiSupporter.callCreatedApi(station, color);
        String uri = createdResponse.header("Location");

        //When
        String updatePostFix = "-600";
        LineRequest updateRequest = new LineRequest(station, color + updatePostFix);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                    .body(updateRequest)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().
                    put(uri)
                .then().log().all()
                .extract();
        LineResponse updatedResponse = ApiSupporter.callFindApi(uri).jsonPath().getObject(".", LineResponse.class);

        //Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedResponse.getColor()).isEqualTo(color+updatePostFix)
        );
    }

    @DisplayName("등록되지 않는 노선에 수정을 요청한다")
    @Test
    void notFoundUpdateLine() {
        //Given
        String station = "1호선";
        String color = "blue";
        LineResponse lineResponse = ApiSupporter.callCreatedApi(station, color).jsonPath().getObject(".", LineResponse.class);

        //When
        String updatePostFix = "-600";
        LineRequest updateRequest = new LineRequest(station, color + updatePostFix);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().
                        put("/lines/"+lineResponse.getId()+1)
                .then().log().all()
                .extract();

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }
}
