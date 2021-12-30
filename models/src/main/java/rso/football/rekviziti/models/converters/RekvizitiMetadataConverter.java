package rso.football.rekviziti.models.converters;

import rso.football.rekviziti.lib.RekvizitiMetadata;
import rso.football.rekviziti.models.entities.RekvizitiMetadataEntity;

public class RekvizitiMetadataConverter {

    public static RekvizitiMetadata toDto(RekvizitiMetadataEntity entity) {

        RekvizitiMetadata dto = new RekvizitiMetadata();
        dto.setRekvizitId(entity.getId());
        dto.setType(entity.getType());
        dto.setCost(entity.getCost());

        return dto;
    }

    public static RekvizitiMetadataEntity toEntity(RekvizitiMetadata dto) {

        RekvizitiMetadataEntity entity = new RekvizitiMetadataEntity();
        entity.setType(dto.getType());
        entity.setCost(dto.getCost());

        return entity;
    }

}