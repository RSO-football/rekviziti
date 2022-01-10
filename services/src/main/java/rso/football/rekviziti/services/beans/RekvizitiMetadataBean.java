package rso.football.rekviziti.services.beans;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import rso.football.rekviziti.lib.RekvizitiMetadata;
import rso.football.rekviziti.models.converters.RekvizitiMetadataConverter;
import rso.football.rekviziti.models.entities.RekvizitiMetadataEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;

@RequestScoped
public class RekvizitiMetadataBean {

    private Logger log = Logger.getLogger(RekvizitiMetadataBean.class.getName());

    @Inject
    private EntityManager em;

    private Client httpClient;
    private String baseUrlUporabniki;

    @PostConstruct
    private void init() {
        String uniqueID = UUID.randomUUID().toString();
        log.info("Inicializacija zrna: " + RekvizitiMetadataBean.class.getSimpleName() + " id: " + uniqueID);

        httpClient = ClientBuilder.newClient();
        baseUrlUporabniki = ConfigurationUtil.getInstance().get("uporabniki-storitev.url").orElse("https://localhost:8083/");
    }

    public List<RekvizitiMetadata> getRekvizitiMetadata() {

        TypedQuery<RekvizitiMetadataEntity> query = em.createNamedQuery(
                "RekvizitiMetadataEntity.getAll", RekvizitiMetadataEntity.class);

        List<RekvizitiMetadataEntity> resultList = query.getResultList();

        return resultList.stream().map(RekvizitiMetadataConverter::toDto).collect(Collectors.toList());

    }


    public List<RekvizitiMetadata> getRekvizitiTrenerjaMetadata(Integer trenerMetadataId) {
        TypedQuery<RekvizitiMetadataEntity> query = em.createNamedQuery(
                "RekvizitiMetadataEntity.getAllTrener", RekvizitiMetadataEntity.class);
        query.setParameter(1, trenerMetadataId);

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

    public String getTrenerjiId(){
        String url = baseUrlUporabniki + "v1/uporabniki/trenerjiId";
        log.info("url je " + url);

        try {
            return httpClient
                    .target(url)
                    .request().get(String.class);
        } catch (WebApplicationException | ProcessingException e){
            throw new InternalServerErrorException(e);
        }
    }

    public RekvizitiMetadata createRekvizitiMetadata(RekvizitiMetadata rekvizitiMetadata) {

        RekvizitiMetadataEntity rekvizitiMetadataEntity = RekvizitiMetadataConverter.toEntity(rekvizitiMetadata);

        String trenerjiString = getTrenerjiId();
        List<Integer> trenerjiId = Arrays.stream(trenerjiString.split(",")).map(Integer::parseInt).collect(Collectors.toList());

        System.out.println(trenerjiString);

        if (!trenerjiId.contains(rekvizitiMetadataEntity.getTrenerId())){
            return null;
        }

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