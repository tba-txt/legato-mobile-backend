package com.floriano.legato_api.services.UserSevice.utils;

import com.floriano.legato_api.dto.UserDTO.UserRequestDTO;
import com.floriano.legato_api.dto.UserDTO.UserUpdateDTO;
import com.floriano.legato_api.model.User.AuxiliaryEntity.ExternalLinks;
import com.floriano.legato_api.model.User.AuxiliaryEntity.Location;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.model.User.enums.Genre;
import com.floriano.legato_api.model.User.enums.InstrumentList;
import com.floriano.legato_api.model.User.enums.UserSex;

import java.util.ArrayList;
import java.util.List;

public class UserUpdateHelper {

    public static void updateBasicFields(User user, UserUpdateDTO dto) {
        if (dto.getProfilePicture() != null)
            user.setProfilePicture(dto.getProfilePicture());

        if (dto.getProfileBanner() != null)
            user.setProfileBanner(dto.getProfileBanner());

        if (dto.getPhotosCard() != null)
            user.setPhotosCard(new ArrayList<>(dto.getPhotosCard()));

        if (dto.getSex() != null)
            user.setSex(UserSex.valueOf(dto.getSex().toUpperCase()));

        if (dto.getBio() != null)
            user.setBio(dto.getBio());

        if (dto.getGoal() != null)
            user.setGoal(dto.getGoal());
    }

    public static void updateMusicPreferences(User user, UserUpdateDTO dto) {
        if (dto.getInstruments() != null) {
            List<InstrumentList> instruments = new ArrayList<>();
            for (String inst : dto.getInstruments()) {
                try {
                    instruments.add(InstrumentList.valueOf(inst.toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
            user.setInstruments(instruments);
        }

        if (dto.getGenres() != null) {
            List<Genre> genres = new ArrayList<>();
            for (String genre : dto.getGenres()) {
                try {
                    genres.add(Genre.valueOf(genre.toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
            user.setGenres(genres);
        }
    }

    public static void updateLocation(User user, UserUpdateDTO dto) {
        if (dto.getLocation() == null) return;

        Location location = user.getLocation();
        if (location == null) location = new Location();

        if(dto.getLocation().getLatitude() != null)
            location.setLatitude(dto.getLocation().getLatitude());

        if(dto.getLocation().getLongitude() != null)
            location.setLongitude(dto.getLocation().getLongitude());


        if (dto.getLocation().getCity() != null)
            location.setCity(dto.getLocation().getCity());
        if (dto.getLocation().getState() != null)
            location.setState(dto.getLocation().getState());
        if (dto.getLocation().getCountry() != null)
            location.setCountry(dto.getLocation().getCountry());

        location.setUpdatedAt(java.time.LocalDateTime.now());

        user.setLocation(location);
    }

    public static void updateExternalLinks(User user, UserUpdateDTO dto) {
        if (dto.getLinks() == null) return;

        ExternalLinks links = user.getLinks();
        if (links == null) links = new ExternalLinks();

        if (dto.getLinks().getInstagram() != null)
            links.setInstagram(dto.getLinks().getInstagram());
        if (dto.getLinks().getSpotify() != null)
            links.setSpotify(dto.getLinks().getSpotify());
        if (dto.getLinks().getYoutube() != null)
            links.setYoutube(dto.getLinks().getYoutube());
        if (dto.getLinks().getWebsite() != null)
            links.setWebsite(dto.getLinks().getWebsite());

        user.setLinks(links);
    }
}

