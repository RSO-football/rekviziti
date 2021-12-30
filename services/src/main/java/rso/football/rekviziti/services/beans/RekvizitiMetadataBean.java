package rso.football.rekviziti.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import rso.football.rekviziti.lib.RekvizitiMetadata;
import rso.football.rekviziti.models.converters.RekvizitiMetadataConverter;
import rso.football.rekviziti.models.entities.RekvizitiMetadataEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class RekvizitiMetadataBean {

    private Logger log = Logger.getLogger(RekvizitiMetadataBean.class.getName());

    @Inject
    private EntityManager em;

    public List<RekvizitiMetadata> getRekvizitiMetadata() {

        TypedQuery<RekvizitiMetadataEntity> query = em.createNamedQuery(
                "RekvizitiMetadataEntity.getAll", RekvizitiMetadataEntity.class);

        List<RekvizitiMetadataEntity> resultList = query.getResultList();

        return resultList.stream().map(RekvizitiMetadataConverter::toDto).collect(Collectors.toList());

    }

    public List<RekvizitiMetadata> getRekvizitiMetadataFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, RekvizitiMetadataEntity.class, queryParameters).stream()
                .map(RekvizitiMetadataConverter::toDto).collect(Collectors.toList());
    }

    public RekvizitiMetadata getRekvizitiMetadata(Integer id) {

        RekvizitiMetadataEntity rekvizitiMetadataEntity = em.find(RekvizitiMetadataEntity.class, id);

        if (rekvizitiMetadataEntity == null) {
            throw new NotFoundException();
        }

        RekvizitiMetadata rekvizitiMetadata = RekvizitiMetadataConverter.toDto(rekvizitiMetadataEntity);

        return rekvizitiMetadata;
    }

    public RekvizitiMetadata createRekvizitiMetadata(RekvizitiMetadata rekvizitiMetadata) {

        RekvizitiMetadataEntity rekvizitiMetadataEntity = RekvizitiMetadataConverter.toEntity(rekvizitiMetadata);

        try {
            beginTx();
            em.persist(rekvizitiMetadataEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (rekvizitiMetadataEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return RekvizitiMetadataConverter.toDto(rekvizitiMetadataEntity);
    }

    public RekvizitiMetadata putRekvizitiMetadata(Integer id, RekvizitiMetadata rekvizitiMetadata) {

        RekvizitiMetadataEntity c = em.find(RekvizitiMetadataEntity.class, id);

        if (c == null) {
            return null;
        }

        RekvizitiMetadataEntity updatedRekvizitiMetadataEntity = RekvizitiMetadataConverter.toEntity(rekvizitiMetadata);

        try {
            beginTx();
            updatedRekvizitiMetadataEntity.setId(c.getId());
            updatedRekvizitiMetadataEntity = em.merge(updatedRekvizitiMetadataEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return RekvizitiMetadataConverter.toDto(updatedRekvizitiMetadataEntity);
    }

    public boolean deleteRekvizitiMetadata(Integer id) {

        RekvizitiMetadataEntity rekvizitiMetadata = em.find(RekvizitiMetadataEntity.class, id);

        if (rekvizitiMetadata != null) {
            try {
                beginTx();
                em.remove(rekvizitiMetadata);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}