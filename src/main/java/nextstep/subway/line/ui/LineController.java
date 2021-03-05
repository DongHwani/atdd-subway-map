package nextstep.subway.line.ui;

import nextstep.subway.line.NotFoundLineException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        LineResponse line = lineService.findById(id);
        return ResponseEntity.ok().body(line);
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity notFoundLineExceptionHandler(NotFoundLineException e) {
        String errorMessage = e.getMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
