package com.chemistry.demo.config.seeder;

import com.chemistry.demo.entity.Features;
import com.chemistry.demo.entity.PackageFeature;
import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.enums.FeatureCode;
import com.chemistry.demo.enums.PackageType;
import com.chemistry.demo.repository.FeatureRepository;
import com.chemistry.demo.repository.PackageFeatureRepository;
import com.chemistry.demo.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PackageDataSeeder implements DataSeeder {

    private final PackageRepository packageRepository;
    private final FeatureRepository featureRepository;
    private final PackageFeatureRepository packageFeatureRepository;

    @Override
    public void seed() {

        if (packageRepository.count() > 0) {
            return;
        }

        /*
         * =========================
         * FEATURES
         * =========================
         */

        Features unlimitedPractice = saveFeature(
                FeatureCode.UNLIMITED_PRACTICE,
                "Unlimited daily practice"
        );

        Features aiExplanation = saveFeature(
                FeatureCode.AI_EXPLANATION,
                "AI chemistry explanation"
        );

        Features arContent = saveFeature(
                FeatureCode.AR_CONTENT,
                "AR chemistry content"
        );

        /*
         * =========================
         * PACKAGES
         * =========================
         */

        Packages freePackage = packageRepository.save(
                Packages.builder()
                        .packageType(PackageType.FREE)
                        .name("Free")
                        .price(BigDecimal.ZERO)
                        .durationDays(99999)
                        .build()
        );

        Packages arLifetime = packageRepository.save(
                Packages.builder()
                        .packageType(PackageType.AR_30_DAYS)
                        .name("AR Access 30 Days")
                        .price(BigDecimal.valueOf(299000))
                        .durationDays(30)
                        .build()
        );

        /*
         * =========================
         * PACKAGE FEATURES
         * =========================
         */

        // FREE
        mapFeature(freePackage, aiExplanation);
        mapFeature(arLifetime, unlimitedPractice);

        // AR LIFETIME
        mapFeature(arLifetime, aiExplanation);
        mapFeature(arLifetime, unlimitedPractice);
        mapFeature(arLifetime, arContent);
    }

    @Override
    public int getOrder() {
        return 4;
    }

    private Features saveFeature(
            FeatureCode code,
            String description
    ) {

        return featureRepository.save(
                Features.builder()
                        .code(code)
                        .description(description)
                        .build()
        );
    }

    private void mapFeature(
            Packages packageEntity,
            Features featureEntity
    ) {

        packageFeatureRepository.save(
                PackageFeature.builder()
                        .packages(packageEntity)
                        .features(featureEntity)
                        .build()
        );
    }
}