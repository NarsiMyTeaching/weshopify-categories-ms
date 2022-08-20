package com.weshopify.platform.features.categories.resources;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.weshopify.platform.features.categories.bean.CategoryPageInfo;
import com.weshopify.platform.features.categories.domain.Category;
import com.weshopify.platform.features.categories.exceptions.CategoryNotFoundException;
import com.weshopify.platform.features.categories.service.CategoryService;

@RestController
public class CategoryRestController {

	@Autowired
	private CategoryService service;
	
	@PostMapping("/categories/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name,
			@Param("alias") String alias) {
		return service.checkUnique(id, name, alias);
	}
	
	@GetMapping("/categories")
	public ResponseEntity<List<Category>> listFirstPage(String sortDir) {
		return listByPage(1, sortDir, null);
	}
	
	@GetMapping("/categories/page/{pageNum}") 
	public ResponseEntity<List<Category>> listByPage(@PathVariable(name = "pageNum") int pageNum, 
			String sortDir,	String keyword) {
		if (sortDir ==  null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
		
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(listCategories);		
	}
	
	
	@PostMapping(value="/categories/save")
	public String saveCategory(Category category, 
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes ra) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);

			Category savedCategory = service.save(category);
			String uploadDir = "category-images/" + savedCategory.getId();
			
			//AmazonS3Util.removeFolder(uploadDir);
			//AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		} else {
			service.save(category);
		}
		
		ra.addFlashAttribute("message", "The category has been saved successfully.");
		return "redirect:/categories";
	}
	
	@GetMapping(value="/categories/{id}/enabled/{status}")
	public ResponseEntity<Category> updateCategoryEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		Category categoryUpdated = service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The category ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(categoryUpdated);
	}
	
	@GetMapping("/categories/delete/{id}")
	public ResponseEntity<List<Category>> deleteCategory(@PathVariable(name = "id") Integer id) {
		try {
			service.delete(id);
			
			
		} catch (CategoryNotFoundException ex) {
			
		}
		
		return listByPage(0, null, null);
	}
	
	/*
	 * @GetMapping("/categories/export/csv") public void
	 * exportToCSV(HttpServletResponse response) throws IOException { List<Category>
	 * listCategories = service.listCategoriesUsedInForm(); CategoryCsvExporter
	 * exporter = new CategoryCsvExporter(); exporter.export(listCategories,
	 * response); }
	 */
}
