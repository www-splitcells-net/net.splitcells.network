package net.splitcells.website;

import java.nio.file.Path;
import java.util.Optional;

public interface Validator {

    Optional<String> validate(Path validationSubject);
}
