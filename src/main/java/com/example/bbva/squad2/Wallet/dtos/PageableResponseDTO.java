package com.example.bbva.squad2.Wallet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PageableResponseDTO<T> {

    private List<T> data; // Lista de elementos paginados
    private int currentPage;
    private int totalPages;
    private String previousPageUrl;
    private String nextPageUrl;

}
