package br.com.rml.SERVICE_NAME.domain.port.out;

import br.com.rml.SERVICE_NAME.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Port OUT — define o que o domínio precisa do mundo externo para persistir
 * dados. O domínio depende desta interface, nunca da implementação concreta
 * (JPA, MongoDB, in-memory...). Implementado pelos adapters de saída
 * (InMemoryProductAdapter, JpaProductAdapter, etc).
 */
public interface ProductRepositoryPort {

	Optional<Product> findById(Long id);

	List<Product> findAll();

	Product save(Product product);

	void deleteById(Long id);

	boolean existsById(Long id);
}
