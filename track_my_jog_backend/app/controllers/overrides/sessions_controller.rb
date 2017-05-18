module Overrides
  class SessionsController < DeviseTokenAuth::SessionsController

    protected

    def render_create_success
      render json: @resource, status: :ok
    end
  end
end
