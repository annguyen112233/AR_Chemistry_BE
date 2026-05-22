package com.chemistry.demo.services.ai;

import java.util.List;

public interface EmbeddingService {

    List<Double> embed(String text);
}
