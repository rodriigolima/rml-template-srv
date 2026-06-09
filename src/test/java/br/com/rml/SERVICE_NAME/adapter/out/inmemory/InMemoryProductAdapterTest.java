package br.com.rml.SERVICE_NAME.adapter.out.inmemory;

import br.com.rml.SERVICE_NAME.domain.model.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste unitário do InMemoryProductAdapter. Sem Spring, sem mock — testa o
 * adapter diretamente.
 */
class InMemoryProductAdapterTest {

	InMemoryProductAdapter adapter;

	@BeforeEach
	void setUp() {
		adapter = new InMemoryProductAdapter();
	}

	@Test
	void save_deveAtribuirIdAutoincremental() {
		Product p1 = adapter.save(new Product(null, "A", BigDecimal.TEN, true));
		Product p2 = adapter.save(new Product(null, "B", BigDecimal.ONE, true));

		assertThat(p1.id()).isEqualTo(1L);
		assertThat(p2.id()).isEqualTo(2L);
	}

	@Test
	void save_deveManterIdExistente_quandoFornecido() {
		Product saved = adapter.save(new Product(42L, "X", BigDecimal.TEN, true));

		assertThat(saved.id()).isEqualTo(42L);
	}

	@Test
	void findById_deveRetornarProduto_quandoExiste() {
		adapter.save(new Product(null, "Monitor", new BigDecimal("1200"), true));

		Optional<Product> result = adapter.findById(1L);

		assertThat(result).isPresent();
		assertThat(result.get().name()).isEqualTo("Monitor");
	}

	@Test
	void findById_deveRetornarVazio_quandoNaoExiste() {
		Optional<Product> result = adapter.findById(999L);

		assertThat(result).isEmpty();
	}

	@Test
	void findAll_deveRetornarTodosOsProdutos() {
		adapter.save(new Product(null, "A", BigDecimal.TEN, true));
		adapter.save(new Product(null, "B", BigDecimal.ONE, false));

		assertThat(adapter.findAll()).hasSize(2);
	}

	@Test
	void deleteById_deveRemoverProduto() {
		adapter.save(new Product(null, "A", BigDecimal.TEN, true));

		adapter.deleteById(1L);

		assertThat(adapter.existsById(1L)).isFalse();
		assertThat(adapter.findAll()).isEmpty();
	}

	@Test
	void existsById_deveRetornarFalse_quandoNaoExiste() {
		assertThat(adapter.existsById(99L)).isFalse();
	}
}
