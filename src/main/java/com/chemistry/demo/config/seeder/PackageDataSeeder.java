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

        Features reaction2D = saveFeature(
                FeatureCode.REACTION_2D,
                "Basic 2D drag and drop reactions"
        );

        Features reaction25DLimited = saveFeature(
                FeatureCode.REACTION_25D_LIMITED,
                "Limited advanced reactions"
        );

        Features reaction25DFull = saveFeature(
                FeatureCode.REACTION_25D_FULL,
                "Full advanced reaction access"
        );

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
                "AR chemistry prototype content"
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

        Packages premiumBasic = packageRepository.save(
                Packages.builder()
                        .packageType(PackageType.PREMIUM_BASIC)
                        .name("Premium Basic")
                        .price(BigDecimal.valueOf(79000))
                        .durationDays(30)
                        .build()
        );

        Packages premiumFull = packageRepository.save(
                Packages.builder()
                        .packageType(PackageType.PREMIUM_FULL)
                        .name("Premium Full")
                        .price(BigDecimal.valueOf(149000))
                        .durationDays(30)
                        .build()
        );

        Packages arLifetime = packageRepository.save(
                Packages.builder()
                        .packageType(PackageType.AR_LIFETIME)
                        .name("AR Lifetime")
                        .price(BigDecimal.valueOf(299000))
                        .durationDays(99999)
                        .build()
        );

        /*
         * =========================
         * PACKAGE FEATURES
         * =========================
         */

        // FREE
        mapFeature(freePackage, reaction2D);
        mapFeature(freePackage, reaction25DLimited);
        mapFeature(freePackage, aiExplanation);

        // PREMIUM BASIC
        mapFeature(premiumBasic, reaction2D);
        mapFeature(premiumBasic, reaction25DLimited);
        mapFeature(premiumBasic, reaction25DFull);
        mapFeature(premiumBasic, unlimitedPractice);
        mapFeature(premiumBasic, aiExplanation);

        // PREMIUM FULL
        mapFeature(premiumFull, reaction2D);
        mapFeature(premiumFull, reaction25DLimited);
        mapFeature(premiumFull, reaction25DFull);
        mapFeature(premiumFull, unlimitedPractice);
        mapFeature(premiumFull, aiExplanation);


        // AR LIFETIME
        mapFeature(arLifetime, reaction2D);
        mapFeature(arLifetime, reaction25DLimited);
        mapFeature(arLifetime, reaction25DFull);
        mapFeature(arLifetime, unlimitedPractice);
        mapFeature(arLifetime, aiExplanation);
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