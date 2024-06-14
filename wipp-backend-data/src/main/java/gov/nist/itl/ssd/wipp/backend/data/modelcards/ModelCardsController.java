package gov.nist.itl.ssd.wipp.backend.data.modelcards;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name="ModelCards Entity")
@RequestMapping(CoreConfig.BASE_URI + "/modelcards/testPath")
public class ModelCardsController
{
    @Autowired
    ModelCardsRepository modelcardsRepository;

    @RequestMapping(value="/test1", method= RequestMethod.GET)
    public String requestOne()
    {
        return "return string test";
    }
}
