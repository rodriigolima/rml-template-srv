package br.com.rml.SERVICE_NAME.domain.port.in;

import br.com.rml.SERVICE_NAME.domain.model.Product;

import java.util.List;

/**
 * Port IN — define o contrato que o domínio expõe para o mundo externo. O
 * controller (adapter IN) só enxerga esta interface, nunca o ProductService.
 * Implementado pelo ProductService.
 */
public interface ManageProductUseCase {

	Product findById(Long id);

	List<Product> findAll();

	Product create(Product product);

	Product update(Long id, Product product);

	void delete(Long id);
}
