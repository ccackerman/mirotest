/**
 * 
 */
package com.miro.widget.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.miro.widget.model.Widget;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WidgetRestControllerTests {

	@LocalServerPort
	private int port;

	private URL base;
	
	private Widget w1 = new Widget(null, 1, 1, null, 10, 20);
	private Widget w2 = new Widget(null, 2, 2, null, 20, 30);

	@Autowired
	private TestRestTemplate template;

    @BeforeEach
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/widgets/");
    }

    @Test
    public void widgetCRUD() throws Exception {
    	//add a widget and get it back
    	template.postForEntity(base.toString(), w1, Widget.class);       
    	ResponseEntity<Widget> response = template.getForEntity(base.toString() + "1", Widget.class);
        Widget expected = new Widget(1l, 1, 1, 0, 10, 20);
        assertThat(response.getBody()).isEqualTo(expected);
        
        //add another and get a list
        template.postForEntity(base.toString(), w2, Widget.class);    	
    	ResponseEntity<Widget[]> listResponse = template.getForEntity(base.toString(), Widget[].class);
    	Widget[] widgets = listResponse.getBody();    			
    	assertNotNull(widgets);
    	assertEquals(2, widgets.length);
        Widget expected2 = new Widget(2l, 2, 2, 1, 20, 30);
        assertEquals(expected, widgets[0]);
        assertEquals(expected2, widgets[1]);
        
        //update a widget and get it back
        Widget w = new Widget(1l, 3, 4, 0, 11, 21);
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<Widget> request = RequestEntity
        	     .put(new URI(base.toString() + "1"))
        	     .accept(MediaType.APPLICATION_JSON)
        	     .body(w);
    	response = template.exchange(base.toString() + "1", HttpMethod.PUT, request, Widget.class);
    	assertThat(response.getBody()).isEqualTo(w);
    	response = template.getForEntity(base.toString() + "1", Widget.class);
        assertThat(response.getBody()).isEqualTo(w);
        
        //delete a widget and it is gone, with others intact
        template.delete(base.toString() + "1");
        response = template.getForEntity(base.toString() + "1", Widget.class);
        assertNull(response.getBody());
        response = template.getForEntity(base.toString() + "2", Widget.class);
        assertThat(response.getBody()).isEqualTo(expected2);
        
        //removing all gives an empty list
        template.delete(base.toString() + "2");
        listResponse = template.getForEntity(base.toString(), Widget[].class);
        widgets = listResponse.getBody();    			
    	assertNotNull(widgets);
    	assertEquals(0, widgets.length);
    }
}
