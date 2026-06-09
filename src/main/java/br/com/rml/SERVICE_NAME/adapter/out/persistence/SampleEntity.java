package br.com.rml.SERVICE_NAME.adapter.out.persistence;

import br.com.rml.common.domain.base.BaseLongEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TODO: Renomear para a entidade JPA real. Ex: UserEntity, ProductEntity
 *
 * Entidade JPA — existe SOMENTE no adapter de persistência.
 * O domínio nunca conhece esta classe.
 * Estende BaseLongEntity do rml-common para ter createdAt e updatedAt.
 */
@Entity
@Table(name = "samples") // TODO: Alterar nome da tabela
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SampleEntity extends BaseLongEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Adicionar campos mapeados para colunas
    @Column(nullable = false)
    private String name;

    @Override
    public Long getId() { return id; }

    @Override
    public void setId(Long id) { this.id = id; }
}
