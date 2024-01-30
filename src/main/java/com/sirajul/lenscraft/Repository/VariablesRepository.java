package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VariablesRepository extends JpaRepository<Variables, Long> {



    List<String> findFrameColorByProduct(Product productId);

    void deleteByProduct(Product productId);

    List<Variables> findAllByProduct(Product product);
}
