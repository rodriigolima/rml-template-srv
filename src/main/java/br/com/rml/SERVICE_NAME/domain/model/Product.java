package br.com.rml.SERVICE_NAME.domain.model;

import java.math.BigDecimal;

/**
 * Modelo de domínio PURO — record Java 21, imutável. Sem anotações de
 * framework, sem JPA, sem Spring. withId() cria uma nova instância preservando
 * imutabilidade.
 */
public record Product(Long id, String name, BigDecimal price, boolean active) {

	public Product withId(Long id) {
		return new Product(id, this.name, this.price, this.active);
	}
}
