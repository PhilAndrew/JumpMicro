package jumpmicro.shared.util.resourceshare.impl

import domino.DominoActivator
import jumpmicro.shared.util.resourceshare.ResourceShareService
import org.log4s.getLogger

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

class ResourceShareServiceImpl extends DominoActivator with ResourceShareService {
  private[this] val logger = getLogger

}
