package nextstep.subway.line.application;

import nextstep.subway.line.NotFoundLineException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class LineServiceTest {

    @Autowired
    private LineService lineService;


    @Test
    public void loadContext(){
        assertThat(lineService).isNotNull();
    }

    @ParameterizedTest
    @CsvSource(value = {"서울역:blue", "일산:yellow", "혜화:green"}, delimiter = ':')
    public void findByIdTest(String stationName, String color) {
        //Given
        LineResponse createdResponse = lineService.saveLine(new LineRequest(stationName, color));

        //When
        LineResponse response = lineService.findById(createdResponse.getId());

        //Then
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(createdResponse.getId()),
                () -> assertThat(response.getName()).isEqualTo(createdResponse.getName()),
                () -> assertThat(response.getCreatedDate()).isEqualTo(createdResponse.getCreatedDate()),
                () -> assertThat(response.getModifiedDate()).isEqualTo(createdResponse.getModifiedDate())
        );
    }

    @Test
    public void notFoundLineException() {
        assertThatThrownBy(() -> {
                 Long id = lineService.saveLine(new LineRequest("대방역", "blue")).getId();
                 lineService.findById(id + 1);
        }).isInstanceOf(NotFoundLineException.class);
    }
}
