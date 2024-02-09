package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.Product.ProductDto;
import com.sirajul.lenscraft.DTO.Product.VariablesDto;
import com.sirajul.lenscraft.Repository.VariablesRepository;
import com.sirajul.lenscraft.Service.interfaces.BrandService;
import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.Service.interfaces.VariableService;
import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.entity.product.enums.FrameSize;
import com.sirajul.lenscraft.mapping.ProductMapping;
import com.sirajul.lenscraft.mapping.VariableMapping;
import com.sirajul.lenscraft.utils.FileUploadUtil;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    ProductService productService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    VariableMapping variableMapping;

    @Autowired
    ProductMapping productMapping;

    @Autowired
    VariableService variableService;


    @GetMapping()
    public String getProductPage(Model model,@RequestParam(name = "keyword",required = false)String keyword) {
        List<Product> productsIn = new ArrayList<>();

        if(keyword!=null){
            productsIn = productService.findAllProductsContaining(keyword);
        }
        else {
            productsIn = productService.findAllProducts();
        }


        model.addAttribute("products", productsIn);
        return "admin/getProducts";
    }

    @GetMapping("/add")
    public String getAddProduct(Model model) {

        ProductDto productDto = new ProductDto();

        List<Brand> brands = brandService.findAllBrandsActive();

        List<Category> categories = categoryService.findAllActiveCategories();

        model.addAttribute("productDto", productDto);
        model.addAttribute("brands", brands);
        model.addAttribute("categories", categories);

        return "admin/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute ProductDto productDto,
                             @RequestParam("brandId") Integer brandId,
                             @RequestParam("categoryId") Long categoryId,
                             @RequestParam("variable.image1")List<MultipartFile> image1,
                             @RequestParam(name = "variable.image2",required = false) List<MultipartFile> image2,
                             @RequestParam(name = "variable.image3",required = false) List<MultipartFile> image3,
                             @RequestParam("variable.frameColor") List<String> frameColor,
                             @RequestParam("variable.quantity") List<Long> quantity
    ) {
        productDto.setBrandId(brandId);
        productDto.setCategoryId(categoryId);
        productDto.setVariables(new ArrayList<>());
        List<VariablesDto> variablesDtos = new ArrayList<>();

        for (int i = 0; i < frameColor.size(); i++) {
            VariablesDto variablesDto = new VariablesDto();

            variablesDto.setFrameColor(frameColor.get(i));
            variablesDto.setQuantity(quantity.get(i));

            if (!image1.get(i).isEmpty()) {
                String filename = StringUtils.cleanPath(image1.get(i).getOriginalFilename());
                variablesDto.setImage1(filename);
            }
            if (!image2.get(i).isEmpty()) {
                String filename = StringUtils.cleanPath(image2.get(i).getOriginalFilename());
                variablesDto.setImage2(filename);
            }
            if (!image3.get(i).isEmpty()) {
                String filename = StringUtils.cleanPath(image3.get(i).getOriginalFilename());
                variablesDto.setImage3(filename);
            }

            variablesDtos.add(variablesDto);
        }





        List<Variables> variables = variableMapping.DtoListToVariable(variablesDtos);

        Product product = productService.saveProductAndGetProduct(productDto, image1, image2, image3);

        for (Variables variables1 : variables) {
            variables1.setProduct(product);
        }
        product.setVariables(variables);

        productService.save(product);

        variableService.saveVariables(variables);


        return "redirect:/admin/products";
    }


    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long productId) {

        productService.deleteById(productId);

        return "redirect:/admin/products";
    }

    @GetMapping("/block/{id}")
    public String blockProduct(
            @PathVariable("id") Long productId
    ){
        productService.blockById(productId);
        return "redirect:/admin/products";
    }

    @GetMapping("/unblock/{id}")
    public String unBlockProduct(
            @PathVariable("id") Long productId
    ){
        productService.unBlockById(productId);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String getEditProduct(@PathVariable("id") Long productId, Model model) {
        Product product = productService.findProductById(productId);
        ProductDto productDto = productMapping.ProductToDto(product);

        List<Variables> variables = variableService.findVariablesByProduct(product);
        System.out.println(variables.isEmpty());


        for (Variables variables1 : variables) {

            System.out.println("id " + variables1.getVariableId());
            System.out.println("quantity" + variables1.getQuantity());
            System.out.println("color" + variables1.getFrameColor());

        }

        model.addAttribute("productDto", productDto);
        model.addAttribute("variables", variables);

        Brand brand = product.getBrand();
        Category category = product.getCategory();

        model.addAttribute("productBrand", brand);
        model.addAttribute("productCategory", category);

        List<Brand> brands = brandService.findAllBrands();
        model.addAttribute("brands", brands);

        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);

        return "admin/edit-product";

    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long productId,
                              @ModelAttribute("productDto") ProductDto productDto,
                              @RequestParam("brandId") Integer brandId,
                              @RequestParam("categoryId") Long categoryId

    ) {
        productDto.setBrandId(brandId);
        productDto.setProductId(productId);
        productDto.setCategoryId(categoryId);

        productService.updateProduct(productDto);


        return "redirect:/admin/products/";

    }

    @GetMapping("/get-variables/{id}")
    public String getEditVariables(@PathVariable("id") Long productId, Model model) {
        Product product = productService.findProductById(productId);
        ProductDto productDto = productMapping.ProductToDto(product);

        for (Variables variable : productDto.getVariables()) {
            System.out.println(variable.getFrameColor());
            System.out.println(variable.getImage1());
            System.out.println(variable.getImage2());
            System.out.println(variable.getImage3());

        }

        model.addAttribute("product", productDto);

        return "admin/get-variables";
    }

    @GetMapping("/edit-variable/{id}")
    public String getEditVariable(@PathVariable("id") Long variableId,Model model) {
        Variables variable = variableService.findVariableById(variableId);

        System.out.println(variable.getVariableId());

        model.addAttribute("variable",variable);

        return "admin/edit-variable";
    }

    @PostMapping("/edit-variable/{id}")
    public String editVariable(@PathVariable("id")Long variableId,@ModelAttribute Variables variable,
                               @RequestParam(name = "variable.image1", required = false) MultipartFile image1,
                               @RequestParam(name = "variable.image2", required = false) MultipartFile image2,
                               @RequestParam(name = "variable.image3", required = false) MultipartFile image3){

        Variables variableFromDB = variableService.findVariableById(variableId);

        variableService.updateVariable(variable,variableFromDB,image1,image2,image3);

        return "redirect:/admin/products/get-variables/"+variableFromDB.getProduct().getProductId();

    }

    @GetMapping("/delete-variable/{id}")
    public String deleteVariable(@PathVariable("id") Long variableId) {
        Variables variable = variableService.findVariableById(variableId);
        Product product = productService.findProductById(variable.getProduct().getProductId());

        product.getVariables().remove(variable);
        productService.save(product);

        System.out.println("remaining variables in the product list" + product.getVariables().size());

        variableService.deleteVariable(variable);
        return "redirect:/admin/products/get-variables/" + product.getProductId();
    }

    @GetMapping("/add-brand")
    public String addBrand() {
        return "redirect:/admin/brand/add";
    }

    @GetMapping("/add-category")
    public String addCat() {
        return "redirect:/admin/category/add";
    }

    @PostMapping("/add-variables/{id}")
    public String addVariables(@PathVariable("id") Long productId,
                               @RequestParam(name = "variable.frameColor",required = false) List<String> frameColor,
                               @RequestParam(name = "variable.image1", required = false) List<MultipartFile> image1,
                               @RequestParam(name = "variable.image2",required = false) List<MultipartFile> image2,
                               @RequestParam(name = "variable.image3",required = false) List<MultipartFile> image3,
                               @RequestParam(name = "variable.quantity",required = false) List<Long> quantity) {
        Product product = productService.findProductById(productId);

        List<Variables> variablesList = new ArrayList<>();
        if(frameColor!=null) {
            for (int i = 0; i < frameColor.size(); i++) {
                Variables variable = new Variables();

                variable.setFrameColor(frameColor.get(i));
                variable.setQuantity(quantity.get(i));

                if (!image1.get(i).isEmpty()) {
                    String fileName = StringUtils.cleanPath(image1.get(i).getOriginalFilename());
                    variable.setImage1(fileName);
                }
                if (!image2.get(i).isEmpty()) {
                    String fileName = StringUtils.cleanPath(image2.get(i).getOriginalFilename());
                    variable.setImage2(fileName);
                }
                if (!image3.get(i).isEmpty()) {
                    String fileName = StringUtils.cleanPath(image3.get(i).getOriginalFilename());
                    variable.setImage3(fileName);
                }

                variable.setProduct(product);
                variableService.saveVariable(variable);

                variablesList.add(variable);

            }

            product.getVariables().addAll(variablesList);


            String uploadDir = "lenscraft/src/main/resources/static/productImages/" + product.getProductId();

            variableService.saveNewVariablesFilesForProduct(uploadDir, image1, image2, image3);

            productService.save(product);
        }

        return "redirect:/admin/products/get-variables/"+productId;
    }


}

