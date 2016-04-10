package edu.jeremiah.sommerfeld.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Card.
 */
@Entity
@Table(name = "card")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "card")
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mana_cost")
    private String manaCost;

    @Column(name = "cmc")
    private Long cmc;

    @Column(name = "type")
    private String type;

    @Column(name = "rarity")
    private String rarity;

    @Column(name = "text")
    private String text;

    @Column(name = "flavor")
    private String flavor;

    @Column(name = "artist")
    private String artist;

    @Column(name = "number")
    private String number;

    @Column(name = "power")
    private Long power;

    @Column(name = "toughness")
    private Long toughness;

    @Column(name = "layout")
    private String layout;

    @Column(name = "multiverseid")
    private String multiverseid;

    @Column(name = "image_name")
    private String imageName;

    @OneToMany(mappedBy = "card")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<SuperType> superTypes = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "card_type",
               joinColumns = @JoinColumn(name="cards_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="types_id", referencedColumnName="ID"))
    private Set<Type> types = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "card_sub_type",
               joinColumns = @JoinColumn(name="cards_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="sub_types_id", referencedColumnName="ID"))
    private Set<SubType> subTypes = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "card_color",
               joinColumns = @JoinColumn(name="cards_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="colors_id", referencedColumnName="ID"))
    private Set<Color> colors = new HashSet<>();

    @ManyToOne
    private CardCollection collection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public Long getCmc() {
        return cmc;
    }

    public void setCmc(Long cmc) {
        this.cmc = cmc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getPower() {
        return power;
    }

    public void setPower(Long power) {
        this.power = power;
    }

    public Long getToughness() {
        return toughness;
    }

    public void setToughness(Long toughness) {
        this.toughness = toughness;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getMultiverseid() {
        return multiverseid;
    }

    public void setMultiverseid(String multiverseid) {
        this.multiverseid = multiverseid;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Set<SuperType> getSuperTypes() {
        return superTypes;
    }

    public void setSuperTypes(Set<SuperType> superTypes) {
        this.superTypes = superTypes;
    }

    public Set<Type> getTypes() {
        return types;
    }

    public void setTypes(Set<Type> types) {
        this.types = types;
    }

    public Set<SubType> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(Set<SubType> subTypes) {
        this.subTypes = subTypes;
    }

    public Set<Color> getColors() {
        return colors;
    }

    public void setColors(Set<Color> colors) {
        this.colors = colors;
    }

    public CardCollection getCollection() {
        return collection;
    }

    public void setCollection(CardCollection cardCollection) {
        this.collection = cardCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card) o;
        if(card.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Card{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", manaCost='" + manaCost + "'" +
            ", cmc='" + cmc + "'" +
            ", type='" + type + "'" +
            ", rarity='" + rarity + "'" +
            ", text='" + text + "'" +
            ", flavor='" + flavor + "'" +
            ", artist='" + artist + "'" +
            ", number='" + number + "'" +
            ", power='" + power + "'" +
            ", toughness='" + toughness + "'" +
            ", layout='" + layout + "'" +
            ", multiverseid='" + multiverseid + "'" +
            ", imageName='" + imageName + "'" +
            '}';
    }
}
