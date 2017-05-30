class TimeEntry < ApplicationRecord
  # Associations
  belongs_to :user

  # Validations
  validates :distance, :duration, :user, presence: true
  validates :distance, :duration, numericality: { greater_than: 0 }
  validate :date_on_or_before_today

  # Scopes for search filtering
  scope :user_id, -> (user_id) { where user_id: user_id }
  scope :date_from, -> (date_from) do
    where("date >= ?", date_from.is_a?(Date) ? date_from : Date.parse(date_from))
  end
  scope :date_to, -> (date_to) do
    where("date <= ?", date_to.is_a?(Date) ? date_to : Date.parse(date_to))
  end

  private

  def date_on_or_before_today
    if self.date.nil?
      errors.add(:date, :blank)
    elsif self.date > Date.today
      errors.add(:date, :cant_be_future)
    end
  end
end
