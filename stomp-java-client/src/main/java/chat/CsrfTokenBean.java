package chat;

import org.springframework.security.web.csrf.CsrfToken;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsrfTokenBean implements CsrfToken {
	
	private static final long serialVersionUID = 1L;
	
	private String headerName;
	private String parameterName;
	private String token;

}
