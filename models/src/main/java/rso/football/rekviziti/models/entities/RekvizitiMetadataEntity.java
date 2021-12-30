package rso.football.rekviziti.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "rekviziti_metadata")
@NamedQueries(value =
        {
                @NamedQuery(name = "RekvizitiMetadataEntity.getAll",
                        query = "SELECT rekvizit FROM RekvizitiMetadataEntity rekvizit")
        })
public class RekvizitiMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private String type;

    @Column(name = "cost")
    private Integer cost;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
}
