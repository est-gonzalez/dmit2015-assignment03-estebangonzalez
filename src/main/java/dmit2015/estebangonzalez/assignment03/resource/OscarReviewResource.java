package dmit2015.estebangonzalez.assignment03.resource;

import common.validator.BeanValidator;
import dmit2015.estebangonzalez.assignment03.entity.OscarReview;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import dmit2015.estebangonzalez.assignment03.repository.OscarReviewRepository;

import java.net.URI;
import java.util.Optional;


/**
 * * Web API with CRUD methods for managing TodoItem.
 *
 *  URI						    Http Method     Request Body		                        Description
 * 	----------------------      -----------		------------------------------------------- ------------------------------------------
 *	/webapi/TodoItems			POST			{"name":"Demo DMIT2015 assignment 1",       Create a new TodoItem
 *                                         	    "complete":false}
 * 	/webapi/TodoItems/{id}		GET			                                                Find one TodoItem with a id value
 * 	/webapi/TodoItems		    GET			                                                Find all TodoItem
 * 	/webapi/TodoItems/{id}      PUT             {"id":5,                                    Update the TodoItem
 * 	                                            "name":"Submitted DMIT2015 assignment 7",
 *                                              "complete":true}
 * 	/webapi/TodoItems/{id}		DELETE			                                            Remove the TodoItem
 *

 curl -i -X GET http://localhost:8080/dmit2015-jaxrs-demo/webapi/TodoItems

 curl -i -X GET http://localhost:8080/dmit2015-jaxrs-demo/webapi/TodoItems/1

 curl -i -X POST http://localhost:8080//dmit2015-jaxrs-demo/webapi/TodoItems \
 -d '{"name":"Finish DMIT2015 Assignment 1","complete":false}' \
 -H 'Content-Type:application/json'

 curl -i -X GET http://localhost:8080/dmit2015-jaxrs-demo/webapi/TodoItems/4

 curl -i -X PUT http://localhost:8080/dmit2015-jaxrs-demo/webapi/TodoItems/4 \
 -d '{"id":4,"name":"Demo DMIT2015 Assignment 1","complete":true}' \
 -H 'Content-Type:application/json'

 curl -i -X GET http://localhost:8080/dmit2015-jaxrs-demo/webapi/TodoItems/4

 curl -i -X DELETE http://localhost:8080/dmit2015-jaxrs-demo/webapi/TodoItems/4

 curl -i -X GET http://localhost:8080/dmit2015-jaxrs-demo/webapi/TodoItems/4

 *
 */

@ApplicationScoped
// This is a CDI-managed bean that is created only once during the life cycle of the application
@Path("OscarReview")	        // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class OscarReviewResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private OscarReviewRepository OscarReviewRepository;

    @POST
    public Response postOscarReview(OscarReview newOscarReview) {
        if (newOscarReview == null) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(OscarReview.class, newOscarReview);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        OscarReviewRepository.create(newOscarReview);
        URI OscarReviewUri = uriInfo.getAbsolutePathBuilder().path(newOscarReview.getId().toString()).build();
        return Response.created(OscarReviewUri).build();
    }

    @GET
    @Path("{id}")
    public Response getOscarReview(@PathParam("id") Long id) {
        Optional<OscarReview> optionalOscarReview = OscarReviewRepository.findOptional(id);

        if (optionalOscarReview.isEmpty()) {
            throw new NotFoundException();
        }
        OscarReview existingOscarReview = optionalOscarReview.get();

        return Response.ok(existingOscarReview).build();
    }

    @GET
    public Response getOscarReviews() {
        return Response.ok(OscarReviewRepository.list()).build();
    }

    @PUT
    @Path("{id}")
    public Response updateOscarReview(@PathParam("id") Long id, OscarReview updatedOscarReview) {
        if (!id.equals(updatedOscarReview.getId())) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(OscarReview.class, updatedOscarReview);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        OscarReview existingOscarReview = OscarReviewRepository
                .findOptional(id)
                .orElseThrow(NotFoundException::new);

        // Copy data from the updated entity to the existing entity
        existingOscarReview.setVersion(updatedOscarReview.getVersion());
        existingOscarReview.setCategory(updatedOscarReview.getCategory());
        existingOscarReview.setNominee(updatedOscarReview.getNominee());
        existingOscarReview.setReview(updatedOscarReview.getReview());
        existingOscarReview.setUsername(updatedOscarReview.getUsername());
        existingOscarReview.setComplete(updatedOscarReview.isComplete());

        try {
            OscarReviewRepository.update(existingOscarReview);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You are updating an old version of the data. Please fetch new version")
                    .build();
        } catch (Exception ex) {
            return Response
                    .serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        return Response.ok(existingOscarReview).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteOscarReview(@PathParam("id") Long id) {
//        Optional<OscarReview> optionalOscarReview= OscarReviewRepository.findOptional(id);
//
//        if (optionalOscarReview.isEmpty()) {
//            throw new NotFoundException();
//        }
//
//        OscarReviewRepository.delete(id);
        OscarReview existingOscarReview = OscarReviewRepository
                .findOptional(id)
                .orElseThrow(NotFoundException::new);
        OscarReviewRepository.remove(existingOscarReview);

        return Response.noContent().build();
    }

}