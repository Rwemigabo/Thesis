/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2;

import com.mycompany.aatr2.monitor.Service;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerStats;
import com.spotify.docker.client.messages.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class Connects to docker and gathers image info about the
 * topologies(yml files) the manager then prompts the feedback control loops to
 * run on each of the containers of a service available in the running topology
 *
 * @author eric
 */
public class DockerManager {

    private final DockerClient cli;
    private List<Image> images;
    private final List<Service> appServices;
    private List<Container> containers;
    private final List<String> monitored;

    private static DockerManager instance;

    public DockerManager() throws DockerCertificateException, DockerException, InterruptedException {
        this.cli = DefaultDockerClient.fromEnv().build();
        this.images = cli.listImages();
        this.monitored = new ArrayList<>();
        this.appServices = new ArrayList<>();
    }

    public static DockerManager getInstance() {
        return instance;
    }

    public void repopulateImagesList() throws DockerException, InterruptedException {
        this.images = cli.listImages();
    }

    public void repopulateContainersList() throws DockerException, InterruptedException {
        this.containers = cli.listContainers();
    }

    public Container getContainer(String id) {
        for (Container cont : containers) {
            if (cont.id().equals(id)) {
                return cont;
            }
        }
        return null;
    }

    private void createMonitors() throws DockerException, InterruptedException {
        repopulateContainersList();
        MonitorManager mm = MonitorManager.getInstance();

        for (Container cont : containers) {
            List<String> temp = new ArrayList();
            appServices.forEach((service) -> {
                temp.add(service.getServName());
            });
            if (!temp.contains(cont.image())) {
                System.out.println("\n Creating new service for " + cont.image());
                Service s = newService(cont.image());
                mm.newMonitor(s);
                this.monitored.add(s.getServName());

            } else {
                System.out.println("\n Already being monitored");
            }
        }
    }

    public Image getImage(String id) {
        for (Image img : images) {
            if (img.id().equals(id)) {
                return img;
            }
        }
        return null;
    }

    public ContainerStats getContainerStats(String id) throws DockerException, InterruptedException {
        ContainerStats stats = cli.stats(id);
        return stats;
    }

    public List<String> getMonitored() {
        return Collections.unmodifiableList(monitored);
    }

    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }

    public List<Container> getContainers() {
        return Collections.unmodifiableList(containers);
    }

    /**
     * Create new service if the string passed to it has no service.
     *
     * @param img image/ service name
     * @return
     */
    public Service newService(String img) {
        Service serv = new Service(img);
        List<String> temp = new ArrayList();
        appServices.forEach((service) -> {
            temp.add(service.getServName());
        });
        if (temp.contains(img)) {
            System.out.print("\n Service already available for this container");
        } else {

            containers.stream().filter((cont) -> (cont.image().equals(img))).forEachOrdered((cont) -> {

                serv.addContainer(cont);
            });
            this.appServices.add(serv);
            return serv;
        }

        return null;
    }

    public static void main(String[] args) {
        try {
            instance = new DockerManager();
            instance.createMonitors();
        } catch (DockerCertificateException | DockerException | InterruptedException ex) {
            Logger.getLogger(DockerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
