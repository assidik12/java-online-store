package toko_online.unit.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toko_online.exception.AppException;
import toko_online.exception.DatabaseException;
import toko_online.exception.InsufficientStockException;
import toko_online.exception.ResourceNotFoundException;
import toko_online.exception.UnauthorizedException;
import toko_online.exception.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHierarchyTest {

    @Test
    @DisplayName("Verify exception hierarchy and error messages")
    void testExceptionHierarchy() {
        ValidationException ve = new ValidationException("Validation failed");
        assertThat(ve.getMessage()).isEqualTo("Validation failed");
        assertThat(ve).isInstanceOf(RuntimeException.class);

        ResourceNotFoundException rne = new ResourceNotFoundException("Not found");
        assertThat(rne.getMessage()).isEqualTo("Not found");
        assertThat(rne).isInstanceOf(AppException.class);

        InsufficientStockException ise = new InsufficientStockException("Out of stock");
        assertThat(ise.getMessage()).isEqualTo("Out of stock");
        assertThat(ise).isInstanceOf(RuntimeException.class);

        UnauthorizedException ue = new UnauthorizedException("Unauthorized");
        assertThat(ue.getMessage()).isEqualTo("Unauthorized");
        assertThat(ue).isInstanceOf(AppException.class);

        DatabaseException de = new DatabaseException("DB error");
        assertThat(de.getMessage()).isEqualTo("DB error");
        assertThat(de).isInstanceOf(AppException.class);
    }
}
